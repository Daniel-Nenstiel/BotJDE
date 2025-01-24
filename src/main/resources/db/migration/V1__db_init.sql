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


-- Create People table
CREATE TABLE IF NOT EXISTS people (
  serverId BIGINT NOT NULL,        -- Discord server ID
  userId BIGINT NOT NULL,          -- Discord user ID
  username TEXT NOT NULL,          -- Username (unique within a server)
  name TEXT NOT NULL,              -- User's name
  PRIMARY KEY (serverId, userId),  -- Composite primary key
  UNIQUE (serverId, username)      -- Ensure username is unique within a server
);

-- Create Events table
CREATE TABLE IF NOT EXISTS events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Unique event ID
  type valid_event NOT NULL,                     -- Event type (ENUM)
  event_date DATE NOT NULL                       -- Event date
);

-- Create People-Events linking table
CREATE TABLE IF NOT EXISTS people_events (
  person_userId BIGINT NOT NULL,        -- References people(userId)
  person_serverId BIGINT NOT NULL,     -- References people(serverId)
  event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  PRIMARY KEY (person_userId, person_serverId, event_id),
  FOREIGN KEY (person_userId, person_serverId)
    REFERENCES people(userId, serverId) ON DELETE CASCADE
);

