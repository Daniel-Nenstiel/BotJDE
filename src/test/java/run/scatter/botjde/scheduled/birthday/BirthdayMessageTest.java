package run.scatter.botjde.scheduled.birthday;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateMono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.User;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BirthdayMessageTest {

  @Mock
  private BirthdayDao birthdayDao;

  @Mock
  private Discord discordUtils;

  @Mock
  private GatewayDiscordClient gatewayDiscordClient;

  @Mock
  private MessageChannel messageChannel;

  @InjectMocks
  private BirthdayMessage birthdayMessage;

  @BeforeEach
  void setUp() {
    try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
      ReflectionTestUtils.setField(birthdayMessage, "channelId", "123456789012345678"); // Set a valid test value
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize mocks", e);
    }
  }

  @Test
  void checkEvent_whenNoBirthdays_doesNotSendMessages() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));

    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      // Ensure the method proceeds by returning false
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, ChronoUnit.SECONDS)).thenReturn(false);

      // Mock no birthdays
      when(birthdayDao.getTodaysBirthdays()).thenReturn(Collections.emptyList());

      // Act
      birthdayMessage.checkEvent();

      // Assert
      verify(birthdayDao).getTodaysBirthdays();
      verifyNoInteractions(discordUtils);
    }
  }

  @Test
  void checkEvent_whenBirthdaysExist_sendsMessages() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));

    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, ChronoUnit.SECONDS)).thenReturn(false);

      // Create a real User instance using the constructor
      User user = new User("Test User", "testuser", 123456789012345678L);

      // Create a Birthday instance and set the user
      Birthday birthday = new Birthday();
      birthday.setUser(user);

      when(birthdayDao.getTodaysBirthdays()).thenReturn(Collections.singletonList(birthday));

      when(discordUtils.getClient()).thenReturn(gatewayDiscordClient);
      when(gatewayDiscordClient.getChannelById(Snowflake.of("123456789012345678")))
          .thenReturn(Mono.just(messageChannel));

      // Mock the behavior of createMessage to return a MessageCreateMono
      MessageCreateMono messageCreateMono = mock(MessageCreateMono.class);
      when(messageChannel.createMessage("Happy Birthday Test User")).thenReturn(messageCreateMono);
      when(messageCreateMono.subscribe()).thenAnswer(invocation -> null);

      // Act
      birthdayMessage.checkEvent();

      // Assert
      verify(birthdayDao).getTodaysBirthdays();
      verify(discordUtils).getClient();
      verify(messageChannel).createMessage("Happy Birthday Test User");
    }
  }

  @Test
  void sendMessage_logsErrorWhenExceptionOccurs() {
    // Arrange
    when(discordUtils.getClient()).thenThrow(new RuntimeException("Test exception"));

    // Act
    birthdayMessage.sendMessage("Test Message");

    // Assert
    verify(discordUtils).getClient();
  }

  @Test
  void formatMessage_returnsDefaultMessage() {
    // Act
    String message = birthdayMessage.formatMessage();

    // Assert
    assertEquals("Happy Birthday", message);
  }

  @Test
  void formatMessage_withBirthday_returnsPersonalizedMessage() {
    // Arrange
    User user = new User("Test User", "testuser", 123456789012345678L); // Real User instance
    Birthday birthday = new Birthday(); // Real Birthday instance
    birthday.setUser(user); // Set the user on the birthday

    // Act
    String message = birthdayMessage.formatMessage(birthday);

    // Assert
    assertEquals("Happy Birthday Test User", message);
  }
}
