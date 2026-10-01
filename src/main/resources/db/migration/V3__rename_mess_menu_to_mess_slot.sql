-- 1. Rename the existing table
ALTER TABLE mess_menu
    RENAME TO mess_slot;


-- 2. Rename columns to the new terminology
ALTER TABLE mess_slot
    RENAME COLUMN type TO meal_type;

ALTER TABLE mess_slot
    RENAME COLUMN week_day TO day_of_week;


-- 3. Rename the existing unique constraint
ALTER TABLE mess_slot
    RENAME CONSTRAINT uc_a3b977a6499b7d890cf008a05
        TO uk_mess_slot_day_meal_type;


-- 4. Rename existing foreign-key constraints
ALTER TABLE menu_extra
    RENAME CONSTRAINT fk_menext_on_extra_item
        TO fk_menu_extra_on_mess_slot;

ALTER TABLE menu_diet
    RENAME CONSTRAINT fk_menu_diet_on_diet_item
        TO fk_menu_diet_on_mess_slot;


-- 5. Rename the FK columns
ALTER TABLE menu_extra
    RENAME COLUMN menu_id TO mess_slot_id;

ALTER TABLE menu_diet
    RENAME COLUMN menu_id TO mess_slot_id;
