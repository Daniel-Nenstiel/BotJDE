-- Needed for UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create Enum type
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'valid_event') THEN
        CREATE TYPE valid_event AS ENUM ('birthday');
    END IF;
END
$$;

-- Create a People table
CREATE TABLE IF NOT EXISTS people (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,          -- Changed from VARCHAR(32) to TEXT
  username TEXT UNIQUE NOT NULL, -- Changed from VARCHAR(32) to TEXT
  userId BIGINT NOT NULL
);

-- Create an Events table
CREATE TABLE IF NOT EXISTS events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  type valid_event NOT NULL,
  event_date DATE NOT NULL
);

-- Create a table for joining People and Events
CREATE TABLE IF NOT EXISTS people_events (
  person_id INT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  PRIMARY KEY (person_id, event_id)
);

-- FUNCTIONS

-- =======================================================
-- Person Scripts
-- =======================================================

-- Function to Insert or Update a Person
CREATE OR REPLACE FUNCTION insert_or_update_person(
    name_param TEXT,
    username_param TEXT,
    userId_param BIGINT
) RETURNS VOID AS $$
BEGIN
    -- Insert the person if they don't exist, or update their name/userId
    IF NOT EXISTS (
        SELECT 1 FROM people WHERE username = username_param
    ) THEN
        INSERT INTO people (name, username, userId)
        VALUES (name_param, username_param, userId_param);
    ELSE
        UPDATE people
        SET name = name_param, userId = userId_param
        WHERE username = username_param;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Function to Get Person ID by Username
CREATE OR REPLACE FUNCTION get_person_id_by_username(
    username TEXT
) RETURNS INT AS $$
DECLARE
    person_id INT;
BEGIN
    SELECT id INTO person_id FROM people WHERE username = username;
    RETURN person_id;
END;
$$ LANGUAGE plpgsql;
