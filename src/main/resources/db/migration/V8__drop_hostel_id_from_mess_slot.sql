ALTER TABLE mess_slot
    DROP COLUMN hostel_id;

ALTER TABLE hostel_menu_plan
    ADD CONSTRAINT FK_HOSTEL_MENU_PLAN_ON_HOSTEL_ID FOREIGN KEY (hostel_id) REFERENCES hostel (id);