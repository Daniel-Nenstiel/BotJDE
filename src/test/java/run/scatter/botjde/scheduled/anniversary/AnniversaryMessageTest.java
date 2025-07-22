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
  void generateMessages_filtersByServer() {
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(456L));

    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice & Bob");
    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of(anniversary));

    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Anniversary to Alice & Bob!");
  }

  @Test
  void generateMessages_returnsEmptyListWhenNoAnniversaries() {
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(456L));

    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of());

    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).isEmpty();
  }

  @Test
  void isEnabled_checksServerConfig() {
    Server mockServer = mock(Server.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(mock(AnniversaryDao.class));

    when(mockServer.isAnniversariesEnabled()).thenReturn(true);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isTrue();

    when(mockServer.isAnniversariesEnabled()).thenReturn(false);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isFalse();
  }

  @Test
  void generateMessages_returnsFormattedMessages() {
    AnniversaryDao anniversaryDao = mock(AnniversaryDao.class);
    AnniversaryMessage anniversaryMessage = new AnniversaryMessage(anniversaryDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(456L));
    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice and Bob");
    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of(anniversary));
    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Anniversary to Alice and Bob!");
  }

}
