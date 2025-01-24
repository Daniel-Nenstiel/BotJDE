package run.scatter.botjde.scheduled.birthday;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.User;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BirthdayMessageTest {

  @Test
  void generateMessages_returnsFormattedMessages() {
    // Mocking BirthdayDao
    BirthdayDao birthdayDao = mock(BirthdayDao.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(birthdayDao);
    AppConfig.Server server = mock(AppConfig.Server.class);

    // Mocking Birthday and its nested properties
    Birthday birthday = mock(Birthday.class);
    User user = mock(User.class); // Replace `User` with the actual class name used in `Birthday`
    when(birthday.getUser()).thenReturn(user);
    when(user.getName()).thenReturn("John");
    when(birthdayDao.getTodaysBirthdays()).thenReturn(List.of(birthday));

    // Execute the method
    List<String> messages = birthdayMessage.generateMessages(server);

    // Verify the results
    assertThat(messages).containsExactly("Happy Birthday John!");
  }


  @Test
  void isEnabled_checksServerConfig() {
    AppConfig.Server server = mock(AppConfig.Server.class);
    BirthdayMessage birthdayMessage = new BirthdayMessage(mock(BirthdayDao.class));

    when(server.isBirthdaysEnabled()).thenReturn(true);
    assertThat(birthdayMessage.isEnabled(server)).isTrue();

    when(server.isBirthdaysEnabled()).thenReturn(false);
    assertThat(birthdayMessage.isEnabled(server)).isFalse();
  }
}
