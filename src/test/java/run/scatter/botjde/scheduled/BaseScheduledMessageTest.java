package run.scatter.botjde.scheduled;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import run.scatter.botjde.entity.server.Server;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseScheduledMessageTest {

  private BaseScheduledMessage message;

  @BeforeEach
  void setup() {
    // Use real method calls for non-overridden methods in BaseScheduledMessage
    message = Mockito.mock(BaseScheduledMessage.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  void checkEvent_whenNotEnabled_returnsEmptyList() {
    // Mock a server
    Server mockServer = mock(Server.class);

    // Simulate the message being disabled for the server
    when(message.isEnabled(mockServer)).thenReturn(false);

    // Execute the method
    List<String> result = message.checkEvent(mockServer);

    // Assert that the result is empty
    assertThat(result).isEmpty();
  }

  @Test
  void checkEvent_whenEnabled_returnsGeneratedMessages() {
    // Mock a server
    Server mockServer = mock(Server.class);

    // Simulate the message being enabled for the server
    when(message.isEnabled(mockServer)).thenReturn(true);

    // Simulate generated messages
    when(message.generateMessages(mockServer)).thenReturn(List.of("Message 1", "Message 2"));

    // Execute the method
    List<String> result = message.checkEvent(mockServer);

    // Assert that the result contains the expected messages
    assertThat(result).containsExactly("Message 1", "Message 2");
  }
}
