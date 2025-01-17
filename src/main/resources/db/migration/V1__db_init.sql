-- Needed for UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create Enum type for valid events
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'valid_event') THEN
        CREATE TYPE valid_event AS ENUM ('birthday');
    END IF;
END
$$;

-- Create Servers table
CREATE TABLE IF NOT EXISTS servers (
  id SERIAL PRIMARY KEY,          -- Auto-incrementing primary key
  serverId BIGINT UNIQUE NOT NULL, -- Unique server ID (e.g., Discord server ID)
  name TEXT NOT NULL              -- Server name
);

-- Create People table
CREATE TABLE IF NOT EXISTS people (
  id SERIAL PRIMARY KEY,          -- Auto-incrementing primary key
  name TEXT NOT NULL,             -- Person's name
  username TEXT UNIQUE NOT NULL,  -- Unique username
  userId BIGINT NOT NULL,         -- Unique user ID (e.g., Discord user ID)
  server_id INT NOT NULL REFERENCES servers(id) ON DELETE CASCADE -- Link to servers table
);

-- Create Events table
CREATE TABLE IF NOT EXISTS events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Unique event ID
  type valid_event NOT NULL,                     -- Event type (ENUM)
  event_date DATE NOT NULL                       -- Event date
);

-- Create People-Events linking table
CREATE TABLE IF NOT EXISTS people_events (
  person_id INT NOT NULL REFERENCES people(id) ON DELETE CASCADE, -- Link to people table
  event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE, -- Link to events table
  PRIMARY KEY (person_id, event_id)                               -- Composite primary key
);
