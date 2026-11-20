begin;

alter table public.products add column if not exists is_active boolean default true;
create table if not exists public.app_product_photos (
  product_id uuid primary key references public.products(id) on delete cascade,
  photo bytea check (
    photo is null or
    (octet_length(photo) between 4 and 196608 and substring(photo from 1 for 3) = decode('ffd8ff', 'hex'))
  )
);

create table if not exists public.app_vendor_profiles (
  vendor_id uuid primary key references public.vendors(id) on delete cascade,
  transport text check (transport in ('walking', 'bicycle', 'motorcycle', 'car', 'van', 'cart')),
  avatar_revision timestamptz,
  avatar bytea check (
    avatar is null or
    (octet_length(avatar) between 4 and 196608 and substring(avatar from 1 for 3) = decode('ffd8ff', 'hex'))
  )
);

create table if not exists public.app_vendor_map_sessions (
  vendor_id uuid primary key references public.vendors(id) on delete cascade,
  session_id uuid not null,
  latitude double precision check (latitude between -90 and 90),
  longitude double precision check (longitude between -180 and 180),
  seen_at timestamptz
);

alter table public.app_vendor_profiles enable row level security;
alter table public.app_vendor_map_sessions enable row level security;
alter table public.app_product_photos enable row level security;
revoke all on public.app_vendor_profiles, public.app_vendor_map_sessions, public.app_product_photos from anon, authenticated;

create or replace function public.app_get_vendor_profile()
returns jsonb language sql stable security definer set search_path = '' as $$
  select coalesce(
    (select jsonb_build_object('transport', p.transport, 'avatar', encode(p.avatar, 'base64'))
       from public.vendors v left join public.app_vendor_profiles p on p.vendor_id = v.id
       where v.user_id = auth.uid() order by v.id limit 1),
    'null'::jsonb
  )
$$;

create or replace function public.app_save_vendor_profile(p_transport text, p_photo_changed boolean, p_photo text)
returns void language plpgsql security definer set search_path = '' as $$
declare v_id uuid;
begin
  select id into v_id from public.vendors where user_id = auth.uid() order by id limit 1;
  if v_id is null then raise exception 'Vendor profile required' using errcode = '42501'; end if;
  if length(p_photo) > 262144 then raise exception 'Photo too large' using errcode = '22023'; end if;
  insert into public.app_vendor_profiles(vendor_id, transport, avatar, avatar_revision)
  values(v_id, p_transport, case when p_photo_changed then decode(p_photo, 'base64') end,
    case when p_photo_changed then statement_timestamp() end)
  on conflict(vendor_id) do update set
    transport = excluded.transport,
    avatar = case when p_photo_changed then excluded.avatar else app_vendor_profiles.avatar end,
    avatar_revision = case when p_photo_changed then excluded.avatar_revision else app_vendor_profiles.avatar_revision end;
end
$$;

create or replace function public.app_get_catalog(p_vendor_id uuid)
returns table(id uuid, name text, price text, has_photo boolean)
language sql stable security definer set search_path = '' as $$
  select p.id, p.name, p.price::text, f.photo is not null
  from public.products p
  left join public.app_product_photos f on f.product_id = p.id
  where p.vendor_id = coalesce(p_vendor_id, (
    select v.id from public.vendors v where v.user_id = auth.uid() order by v.id limit 1
  ))
    and p.is_active is true
  order by p.name, p.id
$$;

create or replace function public.app_get_product_photo(p_product_id uuid)
returns text language sql stable security definer set search_path = '' as $$
  select encode(f.photo, 'base64') from public.app_product_photos f
  join public.products p on p.id = f.product_id
  where p.id = p_product_id and p.is_active is true
$$;

create or replace function public.app_save_product(p_id uuid, p_name text, p_photo_changed boolean, p_photo text)
returns void language plpgsql security definer set search_path = '' as $$
declare v_id uuid;
begin
  select id into v_id from public.vendors where user_id = auth.uid() order by id limit 1;
  if v_id is null then raise exception 'Vendor profile required' using errcode = '42501'; end if;
  if p_id is null or p_name is null or length(btrim(p_name)) not between 1 and 160 then
    raise exception 'Invalid product' using errcode = '22023';
  end if;
  if length(p_photo) > 262144 then raise exception 'Photo too large' using errcode = '22023'; end if;

  insert into public.products as p(id, vendor_id, name, price, is_active)
  values(p_id, v_id, btrim(p_name), 0, true)
  on conflict(id) do update set name = excluded.name where p.vendor_id = v_id and p.is_active;
  if not found then raise exception 'Product not available' using errcode = '42501'; end if;
  if p_photo_changed then
    insert into public.app_product_photos(product_id, photo) values (p_id, decode(p_photo, 'base64'))
    on conflict(product_id) do update set photo = excluded.photo;
  end if;
end
$$;

create or replace function public.app_remove_product(p_id uuid)
returns void language plpgsql security definer set search_path = '' as $$
begin
  update public.products p set is_active = false
  from public.vendors v where p.vendor_id = v.id and v.user_id = auth.uid() and p.id = p_id;
  if not found then raise exception 'Product not available' using errcode = '42501'; end if;
  update public.app_product_photos set photo = null where product_id = p_id;
end
$$;

create or replace function public.app_begin_map_session(p_session_id uuid)
returns void language plpgsql security definer set search_path = '' as $$
declare v_id uuid;
begin
  select id into v_id from public.vendors where user_id = auth.uid() order by id limit 1;
  if v_id is null then raise exception 'Vendor profile required' using errcode = '42501'; end if;
  insert into public.app_vendor_map_sessions(vendor_id, session_id)
  values (v_id, p_session_id)
  on conflict(vendor_id) do update set
    session_id = excluded.session_id, latitude = null, longitude = null, seen_at = null;
end
$$;

create or replace function public.app_publish_map_position(
  p_session_id uuid, p_latitude double precision, p_longitude double precision
)
returns void language plpgsql security definer set search_path = '' as $$
begin
  update public.app_vendor_map_sessions m
  set latitude = p_latitude, longitude = p_longitude, seen_at = statement_timestamp()
  from public.vendors v
  where m.vendor_id = v.id and v.user_id = auth.uid() and m.session_id = p_session_id;
  if not found then raise exception 'Map session ended' using errcode = '42501'; end if;
end
$$;

create or replace function public.app_end_map_session(p_session_id uuid)
returns void language sql security definer set search_path = '' as $$
  delete from public.app_vendor_map_sessions m using public.vendors v
  where m.vendor_id = v.id and v.user_id = auth.uid() and m.session_id = p_session_id
$$;

create or replace function public.app_get_map_markers(p_vendor_id uuid)
returns table(vendor_id uuid, name text, latitude double precision, longitude double precision,
  transport text, has_photo boolean, avatar_revision timestamptz)
language sql stable security definer set search_path = '' as $$
  select v.id, v.name, m.latitude, m.longitude, p.transport, p.avatar is not null, p.avatar_revision
  from public.app_vendor_map_sessions m
  join public.vendors v on v.id = m.vendor_id
  left join public.app_vendor_profiles p on p.vendor_id = v.id
  where m.seen_at > statement_timestamp() - interval '60 seconds' and m.latitude is not null and m.longitude is not null
    and (p_vendor_id is null or v.id = p_vendor_id)
  order by m.seen_at desc
  limit 250
$$;

create or replace function public.app_get_map_avatar(p_vendor_id uuid)
returns text language sql stable security definer set search_path = '' as $$
  select encode(p.avatar, 'base64')
  from public.app_vendor_profiles p join public.app_vendor_map_sessions m on m.vendor_id = p.vendor_id
  where p.vendor_id = p_vendor_id and m.seen_at > statement_timestamp() - interval '60 seconds'
$$;

revoke all on function public.app_get_vendor_profile(), public.app_save_vendor_profile(text, boolean, text),
  public.app_get_catalog(uuid), public.app_get_product_photo(uuid),
  public.app_save_product(uuid, text, boolean, text), public.app_remove_product(uuid),
  public.app_begin_map_session(uuid), public.app_publish_map_position(uuid, double precision, double precision),
  public.app_end_map_session(uuid), public.app_get_map_markers(uuid), public.app_get_map_avatar(uuid)
from public, anon;
grant execute on function public.app_get_vendor_profile(), public.app_save_vendor_profile(text, boolean, text),
  public.app_get_catalog(uuid), public.app_get_product_photo(uuid),
  public.app_save_product(uuid, text, boolean, text), public.app_remove_product(uuid),
  public.app_begin_map_session(uuid), public.app_publish_map_position(uuid, double precision, double precision),
  public.app_end_map_session(uuid), public.app_get_map_markers(uuid), public.app_get_map_avatar(uuid)
to authenticated;

notify pgrst, 'reload schema';
commit;
