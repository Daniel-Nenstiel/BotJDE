ALTER TYPE valid_event ADD VALUE 'anniversary';

-- Function to Add an Anniversary for a Person
CREATE OR REPLACE FUNCTION add_anniversary(
    serverId_param BIGINT,
    userId_param BIGINT,
    event_date_param DATE
) RETURNS VOID AS $$
DECLARE
    event_id UUID;
BEGIN
    -- Insert a new event for the anniversary
    INSERT INTO events (type, event_date)
    VALUES ('anniversary', event_date_param)
    RETURNING id INTO event_id;

    -- Link the user to the event
    INSERT INTO people_events (person_userId, person_serverId, event_id)
    VALUES (userId_param, serverId_param, event_id);
END;
$$ LANGUAGE plpgsql;

-- Function to Add a Shared Anniversary for Multiple People
CREATE OR REPLACE FUNCTION add_shared_anniversary(
    serverId_param BIGINT,
    userIds_param BIGINT[],
    event_date_param DATE
) RETURNS VOID AS $$
DECLARE
    event_id UUID;
    userId BIGINT;
BEGIN
    -- Insert a new event for the shared anniversary
    INSERT INTO events (type, event_date)
    VALUES ('anniversary', event_date_param)
    RETURNING id INTO event_id;

    -- Link each user to the event
    FOREACH userId IN ARRAY userIds_param LOOP
        INSERT INTO people_events (person_userId, person_serverId, event_id)
        VALUES (userId, serverId_param, event_id);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Function to Get All Anniversaries
CREATE OR REPLACE FUNCTION get_all_anniversaries()
RETURNS TABLE (
    event_id UUID,
    anniversary_date DATE,
    users JSONB
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        e.id AS event_id,
        e.event_date AS anniversary_date,
        JSONB_AGG(JSONB_BUILD_OBJECT(
            'serverId', p.serverId,
            'userId', p.userId,
            'username', p.username,
            'name', p.name
        )) AS users
    FROM
        events e
    JOIN
        people_events pe ON e.id = pe.event_id
    JOIN
        people p ON pe.person_userId = p.userId AND pe.person_serverId = p.serverId
    WHERE
        e.type = 'anniversary'
    GROUP BY
        e.id, e.event_date
    ORDER BY
        e.event_date;
END;
$$ LANGUAGE plpgsql;
