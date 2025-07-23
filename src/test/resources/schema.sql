
-- H2-compatible schema for integration tests, based on your migration files

CREATE TABLE people (
  serverId BIGINT NOT NULL,
  userId BIGINT NOT NULL,
  username VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (serverId, userId),
  UNIQUE (serverId, username)
);

CREATE TABLE events (
  id VARCHAR(36) PRIMARY KEY,
  type VARCHAR(32) NOT NULL,
  event_date DATE NOT NULL
);

CREATE TABLE people_events (
  person_userId BIGINT NOT NULL,
  person_serverId BIGINT NOT NULL,
  event_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (person_userId, person_serverId, event_id),
  FOREIGN KEY (person_userId, person_serverId) REFERENCES people(userId, serverId) ON DELETE CASCADE,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

