package run.scatter.botjde.scheduled;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.utils.Discord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageOrchestrator {

  private static final int PUZZLE_MESSAGE_HOUR = 6;
  private static final int TAGGED_MESSAGE_HOUR = 9;

  private final Map<String, ScheduledMessage> messageHandlers;
  private final AppConfig appConfig;
  private final Discord discordUtils;

  public MessageOrchestrator(List<ScheduledMessage> handlers, AppConfig appConfig, Discord discordUtils) {
    this.messageHandlers = handlers.stream()
        .collect(Collectors.toMap(ScheduledMessage::getType, handler -> handler));
    this.appConfig = appConfig;
    this.discordUtils = discordUtils;

    log.info("Registered message handlers: {}", messageHandlers.keySet());
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void schedule() {
    final int currentHour = LocalDateTime.now().getHour();

    switch (currentHour) {
      case PUZZLE_MESSAGE_HOUR -> handleMessages("puzzles");
      case TAGGED_MESSAGE_HOUR -> {
        handleMessages("birthdays");
        handleMessages("anniversaries");
      }
      default -> log.info("No messages scheduled for this hour.");
    }
  }

  protected void handleMessages(String messageType) {
    final ScheduledMessage handler = messageHandlers.get(messageType);

    if (handler == null) {
      log.warn("No handler found for message type: {}", messageType);
      return;
    }

    final Map<Server, List<String>> messagesByServer = appConfig.getServers().stream()
        .collect(Collectors.toMap(
            server -> server,
            handler::checkEvent
        ));

    final List<Server> serversWithMessages = messagesByServer.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .toList();

    if (serversWithMessages.isEmpty()) {
      log.info("No servers have {} messages enabled, skipping Discord login.", messageType);
      return;
    }

    log.info("Starting {} message orchestration for {} servers", messageType, serversWithMessages.size());

    GatewayDiscordClient client = null;
    try {
      client = discordUtils.getClient();

      for (Server server : serversWithMessages) {
        try {
          sendMessagesToServer(client, server, handler, messagesByServer.get(server));
        } catch (Exception e) {
          log.error("Error processing server {}: {}", server.getName(), e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      log.error("Error during {} message orchestration", messageType, e);
    } finally {
      if (client != null) {
        client.logout();
        log.info("Logged out from Discord after {} message orchestration", messageType);
      }
    }
  }

  private void sendMessagesToServer(GatewayDiscordClient client, Server server, ScheduledMessage handler, List<String> messages) {
    final discord4j.common.util.Snowflake channelId = handler.getChannelId(server);

    if (channelId == null) {
      log.error("Handler {} could not determine a valid channel ID for server: {}", handler.getType(), server.getName());
      return;
    }

    messages.forEach(msg -> client.getChannelById(channelId)
        .ofType(MessageChannel.class)
        .flatMap(channel -> channel.createMessage(msg))
        .subscribe(
            success -> log.info("Message sent to channel {} in server {}: {}", channelId.asString(), server.getName(), msg),
            error -> log.error("Failed to send message to channel {} in server {}: {}", channelId.asString(), server.getName(), error.getMessage())
        ));
  }
}
