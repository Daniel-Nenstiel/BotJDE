package run.scatter.botjde.scheduled.anniversary;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.entity.server.Server;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnniversaryMessageTest {

  @Test
  void generateMessages_returnsFormattedMessages() {
    // Mock dependencies
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    Server mockServer = mock(Server.class);

    // Mock anniversary data
    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice and Bob");
    when(anniversaryDao.getTodaysAnniversaries()).thenReturn(List.of(anniversary));

    // Generate messages
    List<String> messages = anniversaryMessage.generateMessages(mockServer);

    // Assert that the message is correctly formatted
    assertThat(messages).containsExactly("Happy Anniversary to Alice and Bob!");
  }

  @Test
  void generateMessages_returnsEmptyListWhenNoAnniversaries() {
    // Mock dependencies
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    Server mockServer = mock(Server.class);

    // No anniversaries for today
    when(anniversaryDao.getTodaysAnniversaries()).thenReturn(List.of());

    // Generate messages
    List<String> messages = anniversaryMessage.generateMessages(mockServer);

    // Assert that no messages are generated
    assertThat(messages).isEmpty();
  }

  @Test
  void isEnabled_checksServerConfig() {
    // Mock server
    Server mockServer = mock(Server.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(mock(AnniversaryDao.class));

    // Check when anniversaries are enabled
    when(mockServer.isAnniversariesEnabled()).thenReturn(true);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isTrue();

    // Check when anniversaries are disabled
    when(mockServer.isAnniversariesEnabled()).thenReturn(false);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isFalse();
  }
}
