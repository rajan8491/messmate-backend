ALTER TABLE mess_slot
    ADD hostel_id BIGINT;

ALTER TABLE mess_slot
    ALTER COLUMN hostel_id SET NOT NULL;

ALTER TABLE mess_slot
    ADD CONSTRAINT fk_mess_slot_hostel
        FOREIGN KEY (hostel_id)
            REFERENCES hostel(id)
            ON DELETE CASCADE;