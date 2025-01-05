-- Needed for UUID
CREATE EXTENSION IF NON EXISTS "pgcrypto";

-- Create Enum type
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'valid_event') THEN
        CREATE TYPE valid_event AS ENUM ('birthday');
    END IF;
END
$$;

-- Create a People table
CREATE TABLE IF NONE EXISTS people (
  id SERIAL PRIMARY KEY,
  name varchar(32) NOT NULL,
  username varchar(32) NOT NULL
);

-- Create an Events table
CREATE TABLE IF NONE EXISTS events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  type valid_event NOT NULL,
  event_date DATE NOT NULL
);

-- Create a table for joining People and Events
CREATE TABLE IF NONE EXISTS people_events (
  PRIMARY KEY (person_id, event_id)
  person_id INT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE
);
