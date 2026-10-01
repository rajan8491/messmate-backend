ALTER TABLE diet_items
    ADD is_active BOOLEAN;

ALTER TABLE diet_items
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE extra_items
    ADD is_active BOOLEAN;

ALTER TABLE extra_items
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE mess_menu
    DROP COLUMN is_active;

ALTER TABLE student_diet
    ALTER COLUMN date SET NOT NULL;

ALTER TABLE student_extra
    ALTER COLUMN date SET NOT NULL;

ALTER TABLE hostel
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE diet_items
    ALTER COLUMN price TYPE DECIMAL
        USING price::DECIMAL;

ALTER TABLE extra_items
    ALTER COLUMN price TYPE DECIMAL
        USING price::DECIMAL;

ALTER TABLE student_diet
    ALTER COLUMN price TYPE DECIMAL
        USING (price::DECIMAL);

ALTER TABLE student_extra
    ALTER COLUMN price TYPE DECIMAL
        USING (price::DECIMAL);

ALTER TABLE student_extra
    ALTER COLUMN price SET NOT NULL;

ALTER TABLE student_extra
    ALTER COLUMN quantity SET NOT NULL;
