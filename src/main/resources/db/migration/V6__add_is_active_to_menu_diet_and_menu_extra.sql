ALTER TABLE menu_diet
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE menu_extra
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE menu_diet
    ADD CONSTRAINT pk_menu_diet PRIMARY KEY (mess_slot_id, diet_id);

ALTER TABLE menu_extra
    ADD CONSTRAINT pk_menu_extra PRIMARY KEY (mess_slot_id, extra_id);

ALTER TABLE diet_items
    DROP COLUMN is_active;

ALTER TABLE extra_items
    DROP COLUMN is_active;