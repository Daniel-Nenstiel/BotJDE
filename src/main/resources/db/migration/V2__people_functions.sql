CREATE OR REPLACE FUNCTION insert_or_update_person(
    serverId_param BIGINT,
    userId_param BIGINT,
    username_param TEXT,
    name_param TEXT
) RETURNS VOID AS $$
BEGIN
    -- Insert or update the person
    IF NOT EXISTS (
        SELECT 1 FROM people WHERE serverId = serverId_param AND userId = userId_param
    ) THEN
        INSERT INTO people (serverId, userId, username, name)
        VALUES (serverId_param, userId_param, username_param, name_param);
    ELSE
        UPDATE people
        SET username = username_param, name = name_param
        WHERE serverId = serverId_param AND userId = userId_param;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_person_by_server_and_user(
    serverId_param BIGINT,
    userId_param BIGINT
) RETURNS TABLE (
    serverId BIGINT,
    userId BIGINT,
    username TEXT,
    name TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT serverId, userId, username, name
    FROM people
    WHERE serverId = serverId_param AND userId = userId_param;
END;
$$ LANGUAGE plpgsql;

