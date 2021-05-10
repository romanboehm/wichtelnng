ALTER TABLE event RENAME COLUMN deadline TO local_date_time;
ALTER TABLE event ADD COLUMN zone_id varchar(50);
UPDATE event SET zone_id = 'UTC'; -- We have to default to UTC since we did not retain the original zone info.
ALTER TABLE event ALTER COLUMN zone_id SET NOT NULL;