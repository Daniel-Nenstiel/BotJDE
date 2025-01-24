package run.scatter.botjde.scheduled;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.utils.Discord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageOrchestrator {
  private static final Logger log = LoggerFactory.getLogger(MessageOrchestrator.class);

  private final Map<String, ScheduledMessage> messageHandlers;
  private final AppConfig appConfig;
  private final Discord discordUtils;

  @Autowired
  public MessageOrchestrator(List<ScheduledMessage> scheduledMessages, AppConfig appConfig, Discord discordUtils) {
    this.messageHandlers = scheduledMessages.stream()
        .collect(Collectors.toMap(ScheduledMessage::getType, handler -> handler));
    this.appConfig = appConfig;
    this.discordUtils = discordUtils;
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void schedule() {
    int currentHour = LocalDateTime.now().getHour();

    switch (currentHour) {
      case 6 -> handleMessages("puzzles");
      case 9 -> {
        handleMessages("birthdays");
        handleMessages("anniversaries");
      }
      default -> {
        //Uncomment
        log.info("No messages scheduled for this hour.");
      }
    }
  }

  protected void handleMessages(String messageType) {
    ScheduledMessage handler = messageHandlers.get(messageType);

    if (handler == null) {
      log.warn("No handler found for message type: {}", messageType);
      return;
    }

    Map<AppConfig.Server, List<String>> messagesByServer = appConfig.getServers().stream()
        .collect(Collectors.toMap(
            server -> server,
            handler::checkEvent
        ));

    List<AppConfig.Server> serversWithMessages = messagesByServer.entrySet().stream()
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

      for (AppConfig.Server server : serversWithMessages) {
        try {
          sendMessagesToServer(client, server, messagesByServer.get(server));
        } catch (Exception e) {
          log.error("Error processing server: {}", server.getName(), e);
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

  private void sendMessagesToServer(GatewayDiscordClient client, AppConfig.Server server, List<String> messages) {
    messages.forEach(msg -> client.getChannelById(server.getDefaultChannelId())
        .ofType(MessageChannel.class)
        .flatMap(channel -> channel.createMessage(msg))
        .subscribe());
  }
}
