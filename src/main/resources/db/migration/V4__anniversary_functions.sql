ALTER TYPE valid_event ADD VALUE 'anniversary';

-- Function to Add an Anniversary for a Person
CREATE OR REPLACE FUNCTION add_anniversary(
    username_param TEXT,  -- Renamed parameter to avoid ambiguity
    eventDate DATE
) RETURNS VOID AS $$
DECLARE
    person_id_var INT;    -- Variable for person ID
    event_id_var UUID;    -- Variable for event ID
BEGIN
    -- Get the person's ID by username
    SELECT id INTO person_id_var FROM people WHERE username = username_param;

    -- Ensure the person exists
    IF person_id_var IS NULL THEN
        RAISE EXCEPTION 'Person with username % does not exist.', username_param;
    END IF;

    -- Insert an anniversary event
    INSERT INTO events (type, event_date)
    VALUES ('anniversary'::valid_event, eventDate)
    RETURNING id INTO event_id_var;

    -- Link the person and the anniversary event
    INSERT INTO people_events (person_id, event_id)
    VALUES (person_id_var, event_id_var);
END;
$$ LANGUAGE plpgsql;

-- Function to Add a Shared Anniversary for Multiple People
CREATE OR REPLACE FUNCTION add_shared_anniversary(
    usernames TEXT[],       -- Array of usernames
    eventDate DATE          -- Date of the anniversary
) RETURNS VOID AS $$
DECLARE
    event_id_var UUID;      -- Variable for shared anniversary event ID
    person_id_var INT;      -- Variable for person ID
    person_username TEXT;   -- Loop variable for usernames
BEGIN
    -- Ensure at least two usernames are provided
    IF array_length(usernames, 1) < 2 THEN
        RAISE EXCEPTION 'At least two usernames are required for a shared anniversary.';
    END IF;

    -- Check if a shared anniversary on the given date already exists for the first user
    SELECT e.id INTO event_id_var
    FROM events e
    JOIN people_events pe ON e.id = pe.event_id
    JOIN people p ON pe.person_id = p.id
    WHERE e.type = 'anniversary'::valid_event
      AND e.event_date = eventDate
      AND p.username = usernames[1];

    -- If no shared event exists on the given date, create one
    IF event_id_var IS NULL THEN
        INSERT INTO events (type, event_date)
        VALUES ('anniversary'::valid_event, eventDate)
        RETURNING id INTO event_id_var;
    END IF;

    -- Link each person in the usernames array to the event
    FOREACH person_username IN ARRAY usernames LOOP
        -- Get the person's ID
        SELECT id INTO person_id_var FROM people WHERE username = person_username;

        -- Ensure the person exists
        IF person_id_var IS NULL THEN
            RAISE EXCEPTION 'Person with username % does not exist.', person_username;
        END IF;

        -- Link the person to the event if not already linked
        IF NOT EXISTS (
            SELECT 1 FROM people_events
            WHERE person_id = person_id_var AND event_id = event_id_var
        ) THEN
            INSERT INTO people_events (person_id, event_id)
            VALUES (person_id_var, event_id_var);
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;


-- Function to Get All Anniversaries
CREATE OR REPLACE FUNCTION get_all_anniversaries()
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
        e.type = 'anniversary'::valid_event;
END;
$$ LANGUAGE plpgsql;
