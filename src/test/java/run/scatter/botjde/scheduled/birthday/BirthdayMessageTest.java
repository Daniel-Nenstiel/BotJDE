package run.scatter.botjde.scheduled.birthday;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.server.Server;
import run.scatter.botjde.entity.User;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BirthdayMessageTest {
  @Test
  void generateMessages_filtersByServer() {
    BirthdayDao birthdayDao = mock(BirthdayDao.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(birthdayDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(123L));

    Birthday birthday = mock(Birthday.class);
    User user = mock(User.class);
    when(birthday.getUser()).thenReturn(user);
    when(user.getName()).thenReturn("John");
    when(birthdayDao.getTodaysBirthdaysForServer("123")).thenReturn(List.of(birthday));

    List<String> messages = birthdayMessage.generateMessages(mockServer);
    assertThat(messages).containsExactly("Happy Birthday John!");
  }

  @Test
  void generateMessages_returnsFormattedMessages() {
    BirthdayDao birthdayDao = mock(BirthdayDao.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(birthdayDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(123L));

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
    BirthdayDao birthdayDao = mock(BirthdayDao.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(birthdayDao);
    Server mockServer = mock(Server.class);
    when(mockServer.getId()).thenReturn(discord4j.common.util.Snowflake.of(123L));
    when(birthdayDao.getTodaysBirthdaysForServer("123")).thenReturn(List.of());
    List<String> messages = birthdayMessage.generateMessages(mockServer);
    assertThat(messages).isEmpty();
  }

  @Test
  void isEnabled_checksServerConfig() {
    // Mock server
    Server mockServer = mock(Server.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(mock(BirthdayDao.class));

    // Check when birthdays are enabled
    when(mockServer.isBirthdaysEnabled()).thenReturn(true);
    assertThat(birthdayMessage.isEnabled(mockServer)).isTrue();

    // Check when birthdays are disabled
    when(mockServer.isBirthdaysEnabled()).thenReturn(false);
    assertThat(birthdayMessage.isEnabled(mockServer)).isFalse();
  }
}
