package run.scatter.botjde.mappers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class BirthdayMapperIntegrationTest {
  @Autowired
  private BirthdayMapper birthdayMapper;

  @Test
  void testGetTodaysBirthdaysForServerMapping() {
    // This will throw if the mapping is broken
    birthdayMapper.getTodaysBirthdaysForServer("1");
  }
}
