ALTER TABLE event RENAME COLUMN local_date_time TO deadline;

UPDATE event
SET deadline = ((deadline AT TIME ZONE zone_id) AT TIME ZONE 'UTC');

ALTER TABLE event DROP COLUMN zone_id;