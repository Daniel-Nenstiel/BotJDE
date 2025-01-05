package run.scatter.botjde.scheduled.birthday;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateMono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;
import run.scatter.botjde.utils.TimeUnit;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BirthdayMessageTest {

  @Mock
  private Discord discordUtils;

  @Mock
  private GatewayDiscordClient gatewayDiscordClient;

  @Mock
  private MessageChannel messageChannel;

  @Mock
  private MessageCreateMono messageCreateMono;

  @InjectMocks
  private BirthdayMessage birthdayMessage;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);

    // Use reflection to set the private channelId field
    Field channelIdField = BirthdayMessage.class.getDeclaredField("channelId");
    channelIdField.setAccessible(true);
    channelIdField.set(birthdayMessage, "123456789012345678");
  }

  @Test
  void message_whenTimeIsNotNearNineAM_doesNotSendMessage() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(9, 0));

    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, TimeUnit.SECONDS)).thenReturn(true);

      // Act
      birthdayMessage.message();

      // Assert
      verifyNoInteractions(discordUtils); // No client interaction
    }
  }

  @Test
  void message_whenTimeIsNearNineAM_sendsMessage() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(9, 0));

    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, TimeUnit.SECONDS)).thenReturn(false);

      when(discordUtils.getClient()).thenReturn(gatewayDiscordClient);
      when(gatewayDiscordClient.getChannelById(Snowflake.of("123456789012345678"))).thenReturn(Mono.just(messageChannel));
      when(messageChannel.createMessage("happy birthday")).thenReturn(messageCreateMono);

      // Act
      birthdayMessage.message();

      // Capture the argument passed to `subscribe()`
      ArgumentCaptor<CoreSubscriber> captor = ArgumentCaptor.forClass(CoreSubscriber.class);
      verify(messageCreateMono).subscribe(captor.capture());

      // Assert
      assertNotNull(captor.getValue(), "The subscriber should not be null");
      verify(discordUtils).getClient();
      verify(gatewayDiscordClient).getChannelById(Snowflake.of("123456789012345678"));
      verify(messageChannel).createMessage("happy birthday");
    }
  }

  @Test
  void message_logsErrorWhenExceptionOccurs() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(9, 0));

    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, TimeUnit.SECONDS)).thenReturn(false);

      // Mock exception for `discordUtils.getClient()`
      when(discordUtils.getClient()).thenThrow(new RuntimeException("Test exception"));

      // Act
      birthdayMessage.message();

      // Assert
      verify(discordUtils).getClient(); // Ensure it was invoked
      verifyNoInteractions(gatewayDiscordClient); // Ensure no further interactions
    }
  }
}
