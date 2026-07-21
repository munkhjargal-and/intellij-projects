UPDATE water_bottle SET vendor_id = 1 WHERE vendor_id IS NULL;
alter table if exists water_bottle alter column vendor_id SET not null;
