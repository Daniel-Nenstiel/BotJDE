package run.scatter.botjde.mappers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class AnniversaryMapperIntegrationTest {
    @Autowired
    private AnniversaryMapper anniversaryMapper;

    @Test
    void testGetTodaysAnniversariesForServerMapping() {
        // This will throw if the mapping is broken
        anniversaryMapper.getTodaysAnniversariesForServer("test-server-id");
    }
}
