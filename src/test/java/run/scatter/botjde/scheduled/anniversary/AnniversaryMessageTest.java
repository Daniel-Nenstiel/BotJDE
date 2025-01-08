package run.scatter.botjde.scheduled.anniversary;

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
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnniversaryMessageTest {

  @Mock
  private AnniversaryDao anniversaryDao;

  @Mock
  private Discord discordUtils;

  @Mock
  private GatewayDiscordClient gatewayDiscordClient;

  @Mock
  private MessageChannel messageChannel;

  @InjectMocks
  private AnniversaryMessage anniversaryMessage;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
      ReflectionTestUtils.setField(anniversaryMessage, "channelId", "123456789012345678");
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize mocks", e);
    }
  }

  @Test
  void checkEvent_whenChannelIdIsNull_logsErrorAndSkips() {
    // Arrange
    ReflectionTestUtils.setField(anniversaryMessage, "channelId", null);

    // Act
    anniversaryMessage.checkEvent();

    // Assert
    verifyNoInteractions(anniversaryDao);
  }

  @Test
  void checkEvent_whenTimeIsNotNearNineAM_skipsExecution() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, ChronoUnit.SECONDS)).thenReturn(false);

      // Act
      anniversaryMessage.checkEvent();

      // Assert
      verifyNoInteractions(anniversaryDao);
      verifyNoInteractions(discordUtils);
    }
  }

  @Test
  void checkEvent_whenAnniversariesExist_sendsMessages() {
    // Arrange
    LocalDateTime nineAM = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
    try (MockedStatic<Time> mockedTime = mockStatic(Time.class)) {
      mockedTime.when(() -> Time.isNowNearTime(nineAM, 30, ChronoUnit.SECONDS)).thenReturn(true);

      Anniversary anniversary = mock(Anniversary.class);
      when(anniversary.getFormattedNames()).thenReturn("John and Jane");
      when(anniversaryDao.getTodaysAnniversaries()).thenReturn(Collections.singletonList(anniversary));

      when(discordUtils.getClient()).thenReturn(gatewayDiscordClient);
      when(gatewayDiscordClient.getChannelById(Snowflake.of("123456789012345678")))
          .thenReturn(Mono.just(messageChannel));

      // Mock the specific return type of createMessage
      MessageCreateMono messageCreateMono = mock(MessageCreateMono.class);
      when(messageChannel.createMessage("Happy Anniversary to John and Jane!"))
          .thenReturn(messageCreateMono);

      // Act
      anniversaryMessage.checkEvent();

      // Assert
      verify(anniversaryDao).getTodaysAnniversaries();
      verify(discordUtils).getClient();
      verify(messageChannel).createMessage("Happy Anniversary to John and Jane!");
    }
  }




  @Test
  void sendMessage_logsErrorWhenExceptionOccurs() {
    // Arrange
    when(discordUtils.getClient()).thenThrow(new RuntimeException("Test exception"));

    // Act
    anniversaryMessage.sendMessage("Test Message");

    // Assert
    verify(discordUtils).getClient();
  }

  @Test
  void sendMessage_skipsEmptyMessage() {
    // Act
    anniversaryMessage.sendMessage("");

    // Assert
    verifyNoInteractions(discordUtils);
  }

  @Test
  void formatMessage_returnsDefaultMessage() {
    // Act
    String message = anniversaryMessage.formatMessage();

    // Assert
    assertEquals("Happy Anniversary!", message);
  }

  @Test
  void formatMessage_withAnniversary_returnsPersonalizedMessage() {
    // Arrange
    Anniversary anniversary = mock(Anniversary.class);
    when(anniversary.getFormattedNames()).thenReturn("John and Jane");

    // Act
    String message = anniversaryMessage.formatMessage(anniversary);

    // Assert
    assertEquals("Happy Anniversary to John and Jane!", message);
  }

  @Test
  void formatMessage_withNullAnniversary_returnsDefaultMessage() {
    // Act
    String message = anniversaryMessage.formatMessage(null);

    // Assert
    assertEquals("Happy Anniversary!", message);
  }
}
