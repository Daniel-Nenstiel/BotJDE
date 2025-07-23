package run.scatter.botjde.mappers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;



@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Sql(statements = {
  "INSERT INTO people (serverId, userId, username, name) VALUES (123456789012345678, 234567890123456789, 'testuser', 'Test User');",
  "INSERT INTO events (id, type, event_date) VALUES ('test-event-id', 'birthday', CURRENT_DATE);",
  "INSERT INTO people_events (person_userId, person_serverId, event_id) VALUES (234567890123456789, 123456789012345678, 'test-event-id');"
})
class BirthdayMapperIntegrationTest {
  @Autowired
  private BirthdayMapper birthdayMapper;

  @Test
  void testGetTodaysBirthdaysForServerMapping() {
    // This will throw if the mapping is broken
    birthdayMapper.getTodaysBirthdaysForServer("123456789012345678");
  }
}
