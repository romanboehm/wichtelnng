ALTER TABLE participant DROP CONSTRAINT participant_pkey;
ALTER TABLE participant DROP COLUMN id;
DROP SEQUENCE hibernate_sequence;