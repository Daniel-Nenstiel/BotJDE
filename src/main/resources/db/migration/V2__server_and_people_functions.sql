CREATE OR REPLACE FUNCTION insert_or_update_server(
    serverId_param BIGINT,
    name_param TEXT
) RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM servers WHERE serverId = serverId_param
    ) THEN
        INSERT INTO servers (serverId, name)
        VALUES (serverId_param, name_param);
    ELSE
        UPDATE servers
        SET name = name_param
        WHERE serverId = serverId_param;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_or_update_person(
    name_param TEXT,
    username_param TEXT,
    userId_param BIGINT,
    serverId_param BIGINT
) RETURNS VOID AS $$
DECLARE
    server_id INT;
BEGIN
    -- Ensure the server exists
    SELECT id INTO server_id FROM servers WHERE serverId = serverId_param;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Server with serverId % does not exist', serverId_param;
    END IF;

    -- Insert or update the person
    IF NOT EXISTS (
        SELECT 1 FROM people WHERE username = username_param
    ) THEN
        INSERT INTO people (name, username, userId, server_id)
        VALUES (name_param, username_param, userId_param, server_id);
    ELSE
        UPDATE people
        SET name = name_param, userId = userId_param, server_id = server_id
        WHERE username = username_param;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_people_by_server(
    serverId_param BIGINT
) RETURNS TABLE (
    person_id INT,
    name TEXT,
    username TEXT,
    userId BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.name, p.username, p.userId
    FROM people p
    JOIN servers s ON p.server_id = s.id
    WHERE s.serverId = serverId_param;
END;
$$ LANGUAGE plpgsql;

