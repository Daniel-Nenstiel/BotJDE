package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.scheduled.tasks.ScheduledTask;

import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * Base class for scheduled tasks that generate and deliver messages to server Discord channels.
 */
@Slf4j
public abstract class BaseScheduledMessage implements ScheduledTask {

  private final AppConfig appConfig;
  private final GatewayDiscordClient gatewayDiscordClient;

  protected BaseScheduledMessage(AppConfig appConfig, @Lazy GatewayDiscordClient gatewayDiscordClient) {
    this.appConfig = appConfig;
    this.gatewayDiscordClient = gatewayDiscordClient;
  }

  @Override
  public void execute() {
    if (gatewayDiscordClient == null) {
      log.warn("GatewayDiscordClient not available, skipping message delivery for '{}'.", getName());
      return;
    }

    if (appConfig == null || appConfig.getServers() == null) {
      log.warn("AppConfig servers list not configured, skipping message delivery for '{}'.", getName());
      return;
    }

    for (Server server : appConfig.getServers()) {
      if (!isEnabled(server)) {
        log.debug("Message task '{}' is disabled for server '{}'", getName(), server.getName());
        continue;
      }

      final List<String> messages = generateMessages(server);
      if (messages.isEmpty()) {
        log.debug("No messages to send for '{}' on server '{}'", getName(), server.getName());
        continue;
      }

      final Snowflake channelId = getChannelId(server);
      if (channelId == null) {
        log.warn("Task '{}' could not determine a valid channel ID for server: {}", getName(), server.getName());
        continue;
      }

      for (String msg : messages) {
        sendMessage(server, channelId, msg);
      }
    }
  }

  protected void sendMessage(Server server, Snowflake channelId, String message) {
    gatewayDiscordClient.getChannelById(channelId)
        .ofType(MessageChannel.class)
        .flatMap(channel -> channel.createMessage(message))
        .subscribe(
            success -> log.info("Message sent to channel {} in server '{}': {}", channelId.asString(), server.getName(), message),
            error -> log.error("Failed to send message to channel {} in server '{}': {}", channelId.asString(), server.getName(), error.getMessage())
        );
  }

  protected abstract boolean isEnabled(Server server);

  protected abstract List<String> generateMessages(Server server);

  public Snowflake getChannelId(Server server) {
    return server.getDefaultChannelId();
  }
}
