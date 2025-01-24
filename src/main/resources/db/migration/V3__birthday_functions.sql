-- Function to Add a Birthday for a Person
CREATE OR REPLACE FUNCTION add_birthday(
    serverId_param BIGINT,
    userId_param BIGINT,
    event_date_param DATE
) RETURNS VOID AS $$
DECLARE
    event_id UUID;
BEGIN
    -- Insert a new event for the birthday
    INSERT INTO events (type, event_date)
    VALUES ('birthday', event_date_param)
    RETURNING id INTO event_id;

    -- Link the user to the event
    INSERT INTO people_events (person_userId, person_serverId, event_id)
    VALUES (userId_param, serverId_param, event_id);
END;
$$ LANGUAGE plpgsql;

-- Function to Get All Birthdays
CREATE OR REPLACE FUNCTION get_all_birthdays()
RETURNS TABLE (
    serverId BIGINT,
    userId BIGINT,
    username TEXT,
    name TEXT,
    birthday_date DATE
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.serverId,
        p.userId,
        p.username,
        p.name,
        e.event_date AS birthday_date
    FROM
        events e
    JOIN
        people_events pe ON e.id = pe.event_id
    JOIN
        people p ON pe.person_userId = p.userId AND pe.person_serverId = p.serverId
    WHERE
        e.type = 'birthday'
    ORDER BY
        e.event_date;
END;
$$ LANGUAGE plpgsql;
