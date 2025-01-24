package run.scatter.botjde.scheduled;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import run.scatter.botjde.config.AppConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseScheduledMessageTest {

  private BaseScheduledMessage message;

  @BeforeEach
  void setup() {
    message = Mockito.mock(BaseScheduledMessage.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  void checkEvent_whenNotEnabled_returnsEmptyList() {
    AppConfig.Server server = mock(AppConfig.Server.class);
    when(message.isEnabled(server)).thenReturn(false);

    List<String> result = message.checkEvent(server);

    assertThat(result).isEmpty();
  }

  @Test
  void checkEvent_whenEnabled_returnsGeneratedMessages() {
    AppConfig.Server server = mock(AppConfig.Server.class);
    when(message.isEnabled(server)).thenReturn(true);
    when(message.generateMessages(server)).thenReturn(List.of("Message 1", "Message 2"));

    List<String> result = message.checkEvent(server);

    assertThat(result).containsExactly("Message 1", "Message 2");
  }
}
