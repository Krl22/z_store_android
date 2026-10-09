-- Panel admin estilo Intu: clientes, notificaciones por tipo, ajustes de tienda y herramientas de cuentas.
-- Además cierra un hueco: un cliente podía cambiar su propio role a 'admin' con PATCH /profiles.

-- 0) ¿La sesión actual es admin? (lo usan las funciones admin_* y las apps)
create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path to ''
as $function$
  select exists (
    select 1 from public.profiles
    where id = (select auth.uid()) and role = 'admin'
  )
$function$;

revoke execute on function public.is_admin() from public, anon;
grant execute on function public.is_admin() to authenticated;

-- 1) El rol solo cambia desde admin_set_role (o desde el SQL Editor), nunca con la API del cliente.
create or replace function public.profiles_guard_role()
returns trigger
language plpgsql
set search_path to ''
as $function$
begin
  if new.role is distinct from old.role and current_user in ('authenticated', 'anon') then
    raise exception 'Solo un administrador puede cambiar roles.' using errcode = '42501';
  end if;
  return new;
end;
$function$;

drop trigger if exists profiles_guard_role on public.profiles;
create trigger profiles_guard_role
  before update on public.profiles
  for each row execute function public.profiles_guard_role();

-- 2) Si se elimina una cuenta, sus pedidos se conservan (sin usuario) para el historial de ventas.
alter table public.orders alter column user_id drop not null;
alter table public.orders drop constraint if exists orders_user_id_fkey;
alter table public.orders add constraint orders_user_id_fkey
  foreign key (user_id) references auth.users (id) on delete set null;

alter table public.orders drop constraint if exists orders_address_id_fkey;
alter table public.orders add constraint orders_address_id_fkey
  foreign key (address_id) references public.customer_addresses (id) on delete set null;

alter table public.profiles drop constraint if exists profiles_default_address_id_fkey;
alter table public.profiles add constraint profiles_default_address_id_fkey
  foreign key (default_address_id) references public.customer_addresses (id) on delete set null;

-- 3) Avisos que no son de un pedido (cliente nuevo, stock bajo).
alter table public.admin_notifications alter column order_id drop not null;
alter table public.admin_notifications add column if not exists kind text not null default 'order';
alter table public.admin_notifications drop constraint if exists admin_notifications_kind_check;
alter table public.admin_notifications add constraint admin_notifications_kind_check
  check (kind in ('order', 'customer', 'stock'));

-- 4) Ajustes de tienda editables desde el panel.
alter table public.store_settings add column if not exists ad_rotation_seconds integer not null default 4;
alter table public.store_settings add column if not exists low_stock_threshold integer not null default 3;
alter table public.store_settings drop constraint if exists store_settings_ad_rotation_check;
alter table public.store_settings add constraint store_settings_ad_rotation_check
  check (ad_rotation_seconds between 2 and 60);
alter table public.store_settings drop constraint if exists store_settings_low_stock_check;
alter table public.store_settings add constraint store_settings_low_stock_check
  check (low_stock_threshold between 0 and 1000);

-- 5) Preferencias de avisos por admin (valen para todos sus dispositivos).
create table if not exists public.admin_notification_preferences (
  admin_id uuid primary key references auth.users (id) on delete cascade,
  enabled boolean not null default true,
  new_orders boolean not null default true,
  new_customers boolean not null default true,
  low_stock boolean not null default true,
  updated_at timestamptz not null default now()
);

alter table public.admin_notification_preferences enable row level security;

drop policy if exists "Admins manage own notification preferences" on public.admin_notification_preferences;
create policy "Admins manage own notification preferences"
  on public.admin_notification_preferences for all
  to authenticated
  using (admin_id = (select auth.uid()) and (select public.is_admin()))
  with check (admin_id = (select auth.uid()) and (select public.is_admin()));

drop trigger if exists admin_notification_preferences_set_updated_at on public.admin_notification_preferences;
create trigger admin_notification_preferences_set_updated_at
  before update on public.admin_notification_preferences
  for each row execute function public.set_updated_at();

create or replace function public.admin_wants_notification(p_admin_id uuid, p_kind text)
returns boolean
language sql
stable
security definer
set search_path to ''
as $function$
  select coalesce((
    select pref.enabled and case p_kind
      when 'order' then pref.new_orders
      when 'customer' then pref.new_customers
      when 'stock' then pref.low_stock
      else true
    end
    from public.admin_notification_preferences pref
    where pref.admin_id = p_admin_id
  ), true)
$function$;

revoke execute on function public.admin_wants_notification(uuid, text) from public, anon, authenticated;

-- Pedido nuevo: respeta las preferencias.
create or replace function public.notify_admins_on_order()
returns trigger
language plpgsql
security definer
set search_path to 'public'
as $function$
begin
  insert into public.admin_notifications (admin_id, order_id, kind, title, body)
  select
    profiles.id,
    new.id,
    'order',
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
  where profiles.role = 'admin'
    and public.admin_wants_notification(profiles.id, 'order');

  return new;
end;
$function$;

-- Cliente nuevo.
create or replace function public.notify_admins_on_customer()
returns trigger
language plpgsql
security definer
set search_path to ''
as $function$
begin
  insert into public.admin_notifications (admin_id, kind, title, body)
  select
    admin.id,
    'customer',
    'Cliente nuevo',
    coalesce(nullif(new.full_name, ''), nullif(new.email, ''), 'Alguien') || ' creó su cuenta en Zeta Dorada.'
  from public.profiles admin
  where admin.role = 'admin'
    and admin.id <> new.id
    and public.admin_wants_notification(admin.id, 'customer');

  return new;
end;
$function$;

revoke execute on function public.notify_admins_on_customer() from public, anon, authenticated;

drop trigger if exists profiles_notify_admins on public.profiles;
create trigger profiles_notify_admins
  after insert on public.profiles
  for each row execute function public.notify_admins_on_customer();

-- Stock bajo: avisa una vez, cuando el stock cruza el umbral hacia abajo.
create or replace function public.notify_admins_on_low_stock()
returns trigger
language plpgsql
security definer
set search_path to ''
as $function$
declare
  v_threshold integer;
begin
  select low_stock_threshold into v_threshold from public.store_settings where id;
  v_threshold = coalesce(v_threshold, 3);

  if new.is_active and new.stock <= v_threshold and old.stock > v_threshold then
    insert into public.admin_notifications (admin_id, kind, title, body)
    select
      admin.id,
      'stock',
      case when new.stock = 0 then 'Producto agotado' else 'Stock bajo' end,
      new.name || ': quedan ' || new.stock || ' unidades.'
    from public.profiles admin
    where admin.role = 'admin'
      and public.admin_wants_notification(admin.id, 'stock');
  end if;

  return new;
end;
$function$;

revoke execute on function public.notify_admins_on_low_stock() from public, anon, authenticated;

drop trigger if exists products_notify_low_stock on public.products;
create trigger products_notify_low_stock
  after update of stock on public.products
  for each row execute function public.notify_admins_on_low_stock();

-- 6) Clientes con su actividad (solo admins).
create or replace function public.admin_list_customers()
returns table (
  id uuid,
  email text,
  full_name text,
  phone text,
  role text,
  created_at timestamptz,
  last_sign_in_at timestamptz,
  email_confirmed boolean,
  providers text[],
  orders_count bigint,
  pending_orders_count bigint,
  total_paid bigint,
  last_order_at timestamptz,
  addresses_count bigint,
  cart_units bigint
)
language plpgsql
stable
security definer
set search_path to ''
as $function$
begin
  if not public.is_admin() then
    raise exception 'Solo para administradores.' using errcode = '42501';
  end if;

  return query
    select
      p.id,
      coalesce(nullif(p.email, ''), u.email::text, ''),
      coalesce(p.full_name, ''),
      coalesce(p.phone, ''),
      p.role,
      p.created_at,
      u.last_sign_in_at,
      u.email_confirmed_at is not null,
      coalesce((select array_agg(distinct i.provider order by i.provider) from auth.identities i where i.user_id = p.id), '{}'),
      (select count(*) from public.orders o where o.user_id = p.id),
      (select count(*) from public.orders o where o.user_id = p.id and o.status = 'pending_payment'),
      coalesce((select sum(o.total) from public.orders o where o.user_id = p.id and o.payment_status = 'paid' and o.status <> 'cancelled'), 0)::bigint,
      (select max(o.created_at) from public.orders o where o.user_id = p.id),
      (select count(*) from public.customer_addresses a where a.user_id = p.id),
      coalesce((select sum(c.quantity) from public.cart_items c where c.user_id = p.id), 0)::bigint
    from public.profiles p
    left join auth.users u on u.id = p.id
    order by p.created_at desc;
end;
$function$;

revoke execute on function public.admin_list_customers() from public, anon;
grant execute on function public.admin_list_customers() to authenticated;

-- 7) Dar o quitar admin. Siempre debe quedar al menos un admin.
create or replace function public.admin_set_role(p_user_id uuid, p_is_admin boolean)
returns void
language plpgsql
security definer
set search_path to ''
as $function$
begin
  if not public.is_admin() then
    raise exception 'Solo para administradores.' using errcode = '42501';
  end if;
  if not exists (select 1 from public.profiles where id = p_user_id) then
    raise exception 'La cuenta no existe.' using errcode = 'P0002';
  end if;
  if not p_is_admin
    and (select role from public.profiles where id = p_user_id) = 'admin'
    and (select count(*) from public.profiles where role = 'admin') <= 1
  then
    raise exception 'Debe quedar al menos un administrador.' using errcode = 'P0001';
  end if;

  update public.profiles
  set role = case when p_is_admin then 'admin' else 'customer' end
  where id = p_user_id;
end;
$function$;

revoke execute on function public.admin_set_role(uuid, boolean) from public, anon;
grant execute on function public.admin_set_role(uuid, boolean) to authenticated;

-- 8) Reiniciar una cuenta para pruebas. Los pedidos se conservan.
--   'cart':    vacía carrito y guardados.
--   'account': lo anterior + direcciones, nombre y teléfono; cancela sus pedidos pendientes de pago (devuelve stock).
create or replace function public.admin_reset_customer(p_user_id uuid, p_scope text)
returns void
language plpgsql
security definer
set search_path to ''
as $function$
begin
  if not public.is_admin() then
    raise exception 'Solo para administradores.' using errcode = '42501';
  end if;
  if p_scope not in ('cart', 'account') then
    raise exception 'Tipo de reinicio inválido.' using errcode = '22023';
  end if;
  if not exists (select 1 from public.profiles where id = p_user_id) then
    raise exception 'La cuenta no existe.' using errcode = 'P0002';
  end if;

  delete from public.cart_items where user_id = p_user_id;
  delete from public.saved_products where user_id = p_user_id;

  if p_scope = 'account' then
    update public.orders set status = 'cancelled'
    where user_id = p_user_id and status = 'pending_payment';

    update public.profiles set default_address_id = null, full_name = '', phone = null
    where id = p_user_id;
    delete from public.customer_addresses where user_id = p_user_id;
  end if;
end;
$function$;

revoke execute on function public.admin_reset_customer(uuid, text) from public, anon;
grant execute on function public.admin_reset_customer(uuid, text) to authenticated;

-- 9) Eliminar una cuenta por completo (login incluido). Los pedidos quedan sin usuario.
create or replace function public.admin_delete_customer(p_user_id uuid)
returns void
language plpgsql
security definer
set search_path to ''
as $function$
begin
  if not public.is_admin() then
    raise exception 'Solo para administradores.' using errcode = '42501';
  end if;
  if not exists (select 1 from auth.users where id = p_user_id) then
    raise exception 'La cuenta no existe.' using errcode = 'P0002';
  end if;
  if (select role from public.profiles where id = p_user_id) = 'admin'
    and (select count(*) from public.profiles where role = 'admin') <= 1
  then
    raise exception 'Es el único administrador: dale admin a otra cuenta antes de eliminarla.' using errcode = 'P0001';
  end if;

  update public.orders set status = 'cancelled'
  where user_id = p_user_id and status = 'pending_payment';

  delete from auth.users where id = p_user_id;
end;
$function$;

revoke execute on function public.admin_delete_customer(uuid) from public, anon;
grant execute on function public.admin_delete_customer(uuid) to authenticated;
