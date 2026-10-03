ALTER TABLE menu_extra
    ADD COLUMN created_at TIMESTAMP(6) WITHOUT TIME ZONE
        DEFAULT CURRENT_TIMESTAMP;

UPDATE menu_extra
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE menu_extra
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE menu_diet
    DROP COLUMN updated_at;