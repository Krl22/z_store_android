-- Checkout real: el pedido se crea "pendiente de pago" y el cliente lo confirma por WhatsApp (pago con Yape).
-- La web y Android comparten el número (store_settings) y el texto del mensaje (order_whatsapp_message).

-- 1) Configuración de la tienda (una sola fila).
create table if not exists public.store_settings (
  id boolean primary key default true check (id),
  whatsapp_number text not null,
  updated_at timestamptz not null default now()
);

alter table public.store_settings enable row level security;

drop policy if exists "Store settings readable by everyone" on public.store_settings;
create policy "Store settings readable by everyone"
  on public.store_settings for select
  to anon, authenticated
  using (true);

drop policy if exists "Admins can update store settings" on public.store_settings;
create policy "Admins can update store settings"
  on public.store_settings for update
  to authenticated
  using (exists (select 1 from public.profiles where id = (select auth.uid()) and role = 'admin'))
  with check (exists (select 1 from public.profiles where id = (select auth.uid()) and role = 'admin'));

drop trigger if exists store_settings_set_updated_at on public.store_settings;
create trigger store_settings_set_updated_at
  before update on public.store_settings
  for each row execute function public.set_updated_at();

insert into public.store_settings (id, whatsapp_number)
values (true, '51928817018')
on conflict (id) do update set whatsapp_number = excluded.whatsapp_number;

-- 2) Nuevo estado "pendiente de pago".
alter table public.orders drop constraint if exists orders_status_check;
alter table public.orders add constraint orders_status_check
  check (status = any (array['pending_payment', 'paid', 'preparing', 'ready', 'completed', 'cancelled']));
alter table public.orders alter column status set default 'pending_payment';
alter table public.orders alter column payment_status set default 'pending';

-- 3) El checkout crea el pedido pendiente de pago (mismo cálculo de precios y stock que antes).
create or replace function public.create_order_with_items(
  p_items jsonb,
  p_address_id text default null,
  p_delivery_method text default 'delivery'
)
returns uuid
language plpgsql
security definer
set search_path to 'public'
as $function$
declare
  v_order_id uuid;
  v_subtotal integer := 0;
  v_item jsonb;
  v_product public.products%rowtype;
  v_quantity integer;
  v_unit_price integer;
  v_address public.customer_addresses%rowtype;
  v_address_id uuid;
  v_address_snapshot jsonb := '{}'::jsonb;
  v_customer_name text;
  v_customer_phone text;
begin
  if (select auth.uid()) is null then
    raise exception 'Debes iniciar sesión para completar el pedido.';
  end if;

  if p_delivery_method not in ('delivery', 'pickup') then
    raise exception 'Método de entrega inválido.';
  end if;

  if jsonb_typeof(p_items) is distinct from 'array' or jsonb_array_length(p_items) = 0 then
    raise exception 'El carrito está vacío.';
  end if;

  if nullif(trim(coalesce(p_address_id, '')), '') is not null
    and lower(trim(p_address_id)) <> 'null'
  then
    v_address_id = trim(p_address_id)::uuid;

    select *
    into v_address
    from public.customer_addresses
    where id = v_address_id
      and user_id = (select auth.uid());

    if not found then
      raise exception 'La dirección no existe o no pertenece a tu cuenta.';
    end if;

    v_address_snapshot = jsonb_build_object(
      'label', v_address.label,
      'recipient_name', v_address.recipient_name,
      'phone', v_address.phone,
      'line1', v_address.line1,
      'line2', v_address.line2,
      'district', v_address.district,
      'city', v_address.city,
      'country', v_address.country,
      'reference', v_address.reference
    );
  end if;

  select
    coalesce(nullif(v_address.recipient_name, ''), nullif(profile.full_name, ''), ''),
    coalesce(nullif(v_address.phone, ''), nullif(profile.phone, ''), '')
  into v_customer_name, v_customer_phone
  from public.profiles profile
  where profile.id = (select auth.uid());

  create temporary table if not exists pg_temp.checkout_items (
    product_id text,
    product_name text,
    quantity integer,
    unit_price integer,
    image_url text
  ) on commit drop;

  truncate table pg_temp.checkout_items;

  for v_item in select * from jsonb_array_elements(p_items)
  loop
    v_quantity = greatest(coalesce((v_item->>'quantity')::integer, 1), 1);

    select *
    into v_product
    from public.products
    where id = v_item->>'product_id'
      and is_active = true
    for update;

    if not found then
      raise exception 'Producto no disponible: %', coalesce(v_item->>'product_name', v_item->>'product_id');
    end if;

    if v_product.stock < v_quantity then
      raise exception 'Stock insuficiente para %. Disponible: %', v_product.name, v_product.stock;
    end if;

    -- Misma regla que Product.currentPrice en Android.
    v_unit_price = case
      when v_product.offer_price is not null
        and v_product.offer_price > 0
        and v_product.offer_price < v_product.price
      then v_product.offer_price
      else v_product.price
    end;

    -- El stock queda reservado; si el pedido se cancela, sync_order_payment_and_stock lo devuelve.
    update public.products
    set stock = stock - v_quantity
    where id = v_product.id;

    insert into pg_temp.checkout_items (product_id, product_name, quantity, unit_price, image_url)
    values (
      v_product.id,
      v_product.name,
      v_quantity,
      v_unit_price,
      coalesce((v_product.image_urls)[1], coalesce(v_product.image_url, ''))
    );

    v_subtotal = v_subtotal + (v_quantity * v_unit_price);
  end loop;

  insert into public.orders (
    user_id,
    customer_email,
    status,
    payment_status,
    subtotal,
    total,
    address_id,
    delivery_method,
    delivery_status,
    customer_name,
    customer_phone,
    delivery_address_snapshot
  )
  values (
    (select auth.uid()),
    coalesce((select auth.jwt()->>'email'), ''),
    'pending_payment',
    'pending',
    v_subtotal,
    v_subtotal,
    v_address_id,
    case when v_address_id is null then 'pickup' else p_delivery_method end,
    'pending',
    coalesce(v_customer_name, ''),
    coalesce(v_customer_phone, ''),
    v_address_snapshot
  )
  returning id into v_order_id;

  insert into public.order_items (order_id, product_id, product_name, quantity, unit_price, image_url)
  select
    v_order_id,
    product_id,
    product_name,
    quantity,
    unit_price,
    image_url
  from pg_temp.checkout_items;

  return v_order_id;
end;
$function$;

-- 4) Cambios de estado: pagado → payment_status = 'paid'; cancelado → se devuelve el stock reservado.
create or replace function public.sync_order_payment_and_stock()
returns trigger
language plpgsql
security definer
set search_path to ''
as $function$
begin
  if new.status is distinct from old.status then
    if old.status = 'pending_payment' and new.status in ('paid', 'preparing', 'ready', 'completed') then
      new.payment_status = 'paid';
    end if;

    if new.status = 'cancelled' and old.status <> 'cancelled' then
      update public.products product
      set stock = product.stock + item.quantity
      from public.order_items item
      where item.order_id = new.id
        and item.product_id = product.id;
    elsif old.status = 'cancelled' and new.status <> 'cancelled' then
      raise exception 'Un pedido cancelado no se puede reabrir; crea uno nuevo.';
    end if;
  end if;

  return new;
end;
$function$;

revoke execute on function public.sync_order_payment_and_stock() from public, anon, authenticated;

drop trigger if exists orders_sync_payment_and_stock on public.orders;
create trigger orders_sync_payment_and_stock
  before update on public.orders
  for each row execute function public.sync_order_payment_and_stock();

-- 5) Aviso a los admins: indica que falta el pago.
create or replace function public.notify_admins_on_order()
returns trigger
language plpgsql
security definer
set search_path to 'public'
as $function$
begin
  insert into public.admin_notifications (admin_id, order_id, title, body)
  select
    profiles.id,
    new.id,
    case when new.status = 'pending_payment' then 'Nuevo pedido (pendiente de pago)' else 'Nuevo pedido' end,
    'Pedido #' || upper(left(new.id::text, 8)) ||
      ' de ' || coalesce(nullif(new.customer_name, ''), nullif(new.customer_email, ''), 'cliente') ||
      ' por S/ ' || new.total::text || '.00' ||
      case
        when new.delivery_method = 'pickup' then ' - recojo'
        when new.customer_phone <> '' then ' - ' || new.customer_phone
        else ''
      end
  from public.profiles
  where profiles.role = 'admin';

  return new;
end;
$function$;

-- 6) Mensaje de WhatsApp del pedido (las apps solo lo codifican y abren wa.me).
create or replace function public.order_whatsapp_message(p_order_id uuid)
returns jsonb
language plpgsql
stable
security invoker
set search_path to 'public'
as $function$
declare
  v_order public.orders%rowtype;
  v_lines text;
  v_delivery text;
  v_phone text;
begin
  -- RLS: el cliente solo ve sus pedidos; los admins ven todos.
  select * into v_order from public.orders where id = p_order_id;
  if not found then
    raise exception 'Pedido no encontrado.';
  end if;

  select whatsapp_number into v_phone from public.store_settings where id;

  select string_agg(
           '• ' || item.quantity || ' × ' || item.product_name || ' — S/ ' || (item.quantity * item.unit_price) || '.00',
           E'\n' order by item.created_at, item.product_name
         )
  into v_lines
  from public.order_items item
  where item.order_id = v_order.id;

  v_delivery = case
    when v_order.delivery_method = 'pickup' then 'Recojo en tienda'
    else 'Delivery a ' || concat_ws(', ',
      nullif(v_order.delivery_address_snapshot->>'line1', ''),
      nullif(v_order.delivery_address_snapshot->>'district', ''),
      nullif(v_order.delivery_address_snapshot->>'city', ''))
  end;

  return jsonb_build_object(
    'phone', coalesce(v_phone, ''),
    'text',
      'Hola, Zeta Dorada. Quiero confirmar mi pedido #' || upper(left(v_order.id::text, 8)) || ':' || E'\n' ||
      coalesce(v_lines, '') || E'\n' ||
      'Total: S/ ' || v_order.total || '.00' || E'\n' ||
      'Entrega: ' || v_delivery || E'\n' ||
      case when v_order.customer_name <> '' then 'Nombre: ' || v_order.customer_name || E'\n' else '' end ||
      'Pago: Yape. ¿Me envían los datos para pagar?'
  );
end;
$function$;

revoke execute on function public.order_whatsapp_message(uuid) from public, anon;
grant execute on function public.order_whatsapp_message(uuid) to authenticated;
