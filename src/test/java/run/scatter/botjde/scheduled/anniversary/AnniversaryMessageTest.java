package run.scatter.botjde.scheduled.anniversary;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnniversaryMessageTest {

  @Test
  void generateMessages_returnsFormattedMessages() {
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    AppConfig.Server server = mock(AppConfig.Server.class);

    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice and Bob");
    when(anniversaryDao.getTodaysAnniversaries()).thenReturn(List.of(anniversary));

    List<String> messages = anniversaryMessage.generateMessages(server);

    assertThat(messages).containsExactly("Happy Anniversary to Alice and Bob!");
  }

  @Test
  void isEnabled_checksServerConfig() {
    AppConfig.Server server = mock(AppConfig.Server.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(mock(AnniversaryDao.class));

    when(server.isAnniversariesEnabled()).thenReturn(true);
    assertThat(anniversaryMessage.isEnabled(server)).isTrue();

    when(server.isAnniversariesEnabled()).thenReturn(false);
    assertThat(anniversaryMessage.isEnabled(server)).isFalse();
  }
}
