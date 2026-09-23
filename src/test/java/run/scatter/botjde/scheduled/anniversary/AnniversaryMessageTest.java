package run.scatter.botjde.scheduled.anniversary;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.persistence.anniversary.dao.AnniversaryDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AnniversaryMessageTest {

  private AnniversaryDao anniversaryDao;
  private AppConfig appConfig;
  private GatewayDiscordClient client;
  private AnniversaryMessage anniversaryMessage;

  @BeforeEach
  void setUp() {
    anniversaryDao = mock(AnniversaryDao.class);
    appConfig = mock(AppConfig.class);
    client = mock(GatewayDiscordClient.class);
    anniversaryMessage = new AnniversaryMessage(anniversaryDao, appConfig, client);
  }

  @Test
  void taskMetadata_returnsExpectedValues() {
    assertThat(anniversaryMessage.getName()).isEqualTo("anniversaries");
    assertThat(anniversaryMessage.getCronExpression()).isEqualTo("0 0 9 * * ?");
  }

  @Test
  void generateMessages_filtersByServer() {
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(Snowflake.of(456L));

    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice & Bob");
    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of(anniversary));

    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Anniversary to Alice & Bob!");
  }

  @Test
  void generateMessages_returnsEmptyListWhenNoAnniversaries() {
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(Snowflake.of(456L));
    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of());

    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).isEmpty();
  }

  @Test
  void isEnabled_checksServerConfig() {
    Server mockServer = mock(Server.class);

    when(mockServer.isAnniversariesEnabled()).thenReturn(true);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isTrue();

    when(mockServer.isAnniversariesEnabled()).thenReturn(false);
    assertThat(anniversaryMessage.isEnabled(mockServer)).isFalse();
  }

  @Test
  void generateMessages_returnsFormattedMessages() {
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(Snowflake.of(456L));
    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("Alice and Bob");
    when(anniversaryDao.getTodaysAnniversariesForServer("456")).thenReturn(List.of(anniversary));

    List<String> messages = anniversaryMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Anniversary to Alice and Bob!");
  }
}
