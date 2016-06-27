UPDATE oskari_maplayer SET version = '' WHERE version is null;
ALTER TABLE oskari_maplayer ALTER COLUMN version SET NOT NULL;