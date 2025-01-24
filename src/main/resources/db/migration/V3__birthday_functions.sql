-- Function to Add a Birthday for a Person
CREATE OR REPLACE FUNCTION add_birthday(
    username_param TEXT,
    eventDate DATE
) RETURNS VOID AS $$
DECLARE
    person_id INT;
    event_id UUID;
BEGIN
    -- Get the person's ID by username
    SELECT id INTO person_id FROM people WHERE username = username_param;

    -- Ensure the person exists
    IF person_id IS NULL THEN
        RAISE EXCEPTION 'Person with username % does not exist.', username_param;
    END IF;

    -- Insert a birthday event
    INSERT INTO events (type, event_date)
    VALUES ('birthday'::valid_event, eventDate)
    RETURNING id INTO event_id;

    -- Link the person and the birthday event
    INSERT INTO people_events (person_id, event_id)
    VALUES (person_id, event_id);
END;
$$ LANGUAGE plpgsql;


-- Function to Get All Birthdays
CREATE OR REPLACE FUNCTION get_all_birthdays()
RETURNS TABLE(name TEXT, username TEXT, event_date DATE) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.name, p.username, e.event_date
    FROM
        people p
    JOIN
        people_events pe ON p.id = pe.person_id
    JOIN
        events e ON pe.event_id = e.id
    WHERE
        e.type = 'birthday'::valid_event;
END;
$$ LANGUAGE plpgsql;
