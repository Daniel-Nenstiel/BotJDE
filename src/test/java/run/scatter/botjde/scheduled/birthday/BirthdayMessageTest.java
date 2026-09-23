package run.scatter.botjde.scheduled.birthday;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.entity.User;
import run.scatter.botjde.persistence.birthday.dao.BirthdayDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BirthdayMessageTest {

  private BirthdayDao birthdayDao;
  private AppConfig appConfig;
  private GatewayDiscordClient client;
  private BirthdayMessage birthdayMessage;

  @BeforeEach
  void setUp() {
    birthdayDao = mock(BirthdayDao.class);
    appConfig = mock(AppConfig.class);
    client = mock(GatewayDiscordClient.class);
    birthdayMessage = new BirthdayMessage(birthdayDao, appConfig, client);
  }

  @Test
  void taskMetadata_returnsExpectedValues() {
    assertThat(birthdayMessage.getName()).isEqualTo("birthdays");
    assertThat(birthdayMessage.getCronExpression()).isEqualTo("0 0 9 * * ?");
  }

  @Test
  void generateMessages_filtersByServer() {
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(Snowflake.of(123L));

    Birthday birthday = mock(Birthday.class);
    User user = mock(User.class);
    when(birthday.getUser()).thenReturn(user);
    when(user.getName()).thenReturn("John");
    when(birthdayDao.getTodaysBirthdaysForServer("123")).thenReturn(List.of(birthday));

    List<String> messages = birthdayMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Birthday John!");
  }

  @Test
  void generateMessages_returnsEmptyListWhenNoBirthdays() {
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(Snowflake.of(123L));
    when(birthdayDao.getTodaysBirthdaysForServer("123")).thenReturn(List.of());

    List<String> messages = birthdayMessage.generateMessages(mockServer);
    assertThat(messages).isEmpty();
  }

  @Test
  void isEnabled_checksServerConfig() {
    Server mockServer = mock(Server.class);

    when(mockServer.isBirthdaysEnabled()).thenReturn(true);
    assertThat(birthdayMessage.isEnabled(mockServer)).isTrue();

    when(mockServer.isBirthdaysEnabled()).thenReturn(false);
    assertThat(birthdayMessage.isEnabled(mockServer)).isFalse();
  }
}
