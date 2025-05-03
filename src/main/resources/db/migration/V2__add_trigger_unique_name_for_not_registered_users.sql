CREATE OR REPLACE FUNCTION unique_name_for_not_registered_users()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM users u
                        JOIN not_registered_users nru ON u.id = nru.user_id
               WHERE u.name = (SELECT name FROM users WHERE id = NEW.user_id)) THEN
        RAISE EXCEPTION 'Name "%" is already taken by a not-registered user.', NEW.name;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_name_for_not_registered_users
    BEFORE INSERT OR UPDATE
    ON not_registered_users
    FOR EACH ROW
EXECUTE FUNCTION unique_name_for_not_registered_users();