package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateMono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseScheduledMessageTest {

  private AppConfig mockAppConfig;
  private GatewayDiscordClient mockClient;
  private MessageChannel mockChannel;
  private MessageCreateMono mockMessageMono;
  private TestScheduledMessage messageTask;

  private static class TestScheduledMessage extends BaseScheduledMessage {
    private boolean enabled = true;
    private List<String> messages = List.of("Test Message 1");

    TestScheduledMessage(AppConfig appConfig, GatewayDiscordClient client) {
      super(appConfig, client);
    }

    @Override
    public String getName() {
      return "testMessage";
    }

    @Override
    public String getCronExpression() {
      return "0 0 12 * * ?";
    }

    @Override
    protected boolean isEnabled(Server server) {
      return enabled;
    }

    @Override
    protected List<String> generateMessages(Server server) {
      return messages;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public void setMessages(List<String> messages) {
      this.messages = messages;
    }
  }

  @BeforeEach
  void setUp() {
    mockAppConfig = mock(AppConfig.class);
    mockClient = mock(GatewayDiscordClient.class);
    mockChannel = mock(MessageChannel.class);
    mockMessageMono = mock(MessageCreateMono.class);

    when(mockChannel.createMessage(anyString())).thenReturn(mockMessageMono);
    when(mockClient.getChannelById(any(Snowflake.class))).thenReturn(Mono.just(mockChannel));

    messageTask = new TestScheduledMessage(mockAppConfig, mockClient);
  }

  @Test
  void taskMetadata_returnsExpectedValues() {
    assertThat(messageTask.getName()).isEqualTo("testMessage");
    assertThat(messageTask.getCronExpression()).isEqualTo("0 0 12 * * ?");
  }

  @Test
  void execute_whenDisabled_doesNotSendMessage() {
    Server server = mock(Server.class);
    when(server.getDefaultChannelId()).thenReturn(Snowflake.of(123L));
    when(mockAppConfig.getServers()).thenReturn(List.of(server));

    messageTask.setEnabled(false);
    messageTask.execute();

    verify(mockClient, never()).getChannelById(any(Snowflake.class));
  }

  @Test
  void execute_whenEnabled_sendsMessages() {
    Server server = mock(Server.class);
    when(server.getDefaultChannelId()).thenReturn(Snowflake.of(123L));
    when(mockAppConfig.getServers()).thenReturn(List.of(server));

    messageTask.setEnabled(true);
    messageTask.setMessages(List.of("Hello Discord!"));
    messageTask.execute();

    verify(mockClient, times(1)).getChannelById(Snowflake.of(123L));
    verify(mockChannel, times(1)).createMessage("Hello Discord!");
  }
}
