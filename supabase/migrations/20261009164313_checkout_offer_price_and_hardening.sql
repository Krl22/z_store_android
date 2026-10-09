-- 1) El checkout cobra el precio de oferta vigente (el mismo que muestran las apps).
-- 2) Los pedidos solo se crean vía create_order_with_items: se quitan los INSERT directos.
-- 3) El secreto del webhook de push sale del trigger y pasa a Vault.

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
begin
  if (select auth.uid()) is null then
    raise exception 'Debes iniciar sesion para completar el checkout.';
  end if;

  if p_delivery_method not in ('delivery', 'pickup') then
    raise exception 'Metodo de entrega invalido.';
  end if;

  if jsonb_typeof(p_items) is distinct from 'array' or jsonb_array_length(p_items) = 0 then
    raise exception 'El carrito esta vacio.';
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
      raise exception 'La direccion no existe o no pertenece a tu cuenta.';
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
    'paid',
    'simulated_paid',
    v_subtotal,
    v_subtotal,
    v_address_id,
    case when v_address_id is null then 'pickup' else p_delivery_method end,
    case when v_address_id is null then 'ready' else 'pending' end,
    coalesce(v_address.recipient_name, ''),
    coalesce(v_address.phone, ''),
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

drop policy if exists "Users can insert own orders" on public.orders;
drop policy if exists "Users can insert own order items" on public.order_items;

create or replace function public.dispatch_admin_order_push()
returns trigger
language plpgsql
security definer
set search_path to ''
as $function$
declare
  v_secret text;
begin
  select decrypted_secret
  into v_secret
  from vault.decrypted_secrets
  where name = 'admin_push_webhook_secret';

  if v_secret is null then
    raise warning 'admin_push_webhook_secret no existe en Vault; no se envia push.';
    return new;
  end if;

  perform net.http_post(
    url := 'https://ijlmtgmdibrdjhqpiygv.supabase.co/functions/v1/send-admin-order-push',
    body := jsonb_build_object(
      'type', 'INSERT',
      'table', 'admin_notifications',
      'schema', 'public',
      'record', to_jsonb(new),
      'old_record', null
    ),
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'x-webhook-secret', v_secret
    ),
    timeout_milliseconds := 5000
  );

  return new;
end;
$function$;

revoke execute on function public.dispatch_admin_order_push() from public, anon, authenticated;

drop trigger if exists "send-admin-order-push" on public.admin_notifications;
create trigger admin_notifications_send_push
  after insert on public.admin_notifications
  for each row execute function public.dispatch_admin_order_push();
