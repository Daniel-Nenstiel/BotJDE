package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;
import run.scatter.botjde.gameservers.service.GameServerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Slash command /server for managing game servers (status, start, stop, restart, logs).
 */
@Slf4j
@Component
public class GameServerCommand implements SlashCommand {

  private static final int MAX_LOG_LENGTH = 1900;
  private static final int DEFAULT_LOG_LINES = 25;

  private final GameServerService gameServerService;

  public GameServerCommand(GameServerService gameServerService) {
    this.gameServerService = gameServerService;
  }

  @Override
  public String getName() {
    return "server";
  }

  @Override
  public ApplicationCommandRequest getCommandDefinition() {
    final List<ApplicationCommandOptionData> options = new ArrayList<>();

    // Top-level subcommand: /server status (view all servers)
    options.add(ApplicationCommandOptionData.builder()
        .name("status")
        .description("View status of all configured game servers")
        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
        .build());

    // Subcommand groups for each configured game server: /server <game> <action>
    for (GameServerConfig server : gameServerService.getEnabledServers()) {
      final List<ApplicationCommandOptionData> actions = List.of(
          ApplicationCommandOptionData.builder()
              .name("status")
              .description("Check status of " + server.getName())
              .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
              .build(),
          ApplicationCommandOptionData.builder()
              .name("start")
              .description("Start " + server.getName())
              .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
              .build(),
          ApplicationCommandOptionData.builder()
              .name("stop")
              .description("Stop " + server.getName())
              .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
              .build(),
          ApplicationCommandOptionData.builder()
              .name("restart")
              .description("Restart " + server.getName())
              .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
              .build(),
          ApplicationCommandOptionData.builder()
              .name("logs")
              .description("View recent logs for " + server.getName())
              .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
              .addOption(ApplicationCommandOptionData.builder()
                  .name("lines")
                  .description("Number of lines to fetch (default: 25)")
                  .type(ApplicationCommandOption.Type.INTEGER.getValue())
                  .required(false)
                  .build())
              .build()
      );

      options.add(ApplicationCommandOptionData.builder()
          .name(server.getId().toLowerCase())
          .description("Manage " + server.getName())
          .type(ApplicationCommandOption.Type.SUB_COMMAND_GROUP.getValue())
          .options(actions)
          .build());
    }

    return ApplicationCommandRequest.builder()
        .name(getName())
        .description("Control and monitor hosted game servers")
        .options(options)
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.deferReply()
        .then(routeCommand(event));
  }

  private Mono<Void> routeCommand(ChatInputInteractionEvent event) {
    final List<ApplicationCommandInteractionOption> options = event.getOptions();
    if (options.isEmpty()) {
      return event.editReply("Please specify a subcommand (e.g. `/server status` or `/server zomboid start`).").then();
    }

    final ApplicationCommandInteractionOption firstOption = options.get(0);
    final String optionName = firstOption.getName();

    // /server status
    if ("status".equalsIgnoreCase(optionName) && firstOption.getOptions().isEmpty()) {
      return handleOverallStatus(event);
    }

    // /server <game> <action>
    final String serverId = optionName;
    if (firstOption.getOptions().isEmpty()) {
      return event.editReply("Please specify an action for `" + serverId + "` (status, start, stop, restart, logs).").then();
    }

    final ApplicationCommandInteractionOption actionOption = firstOption.getOptions().get(0);
    final String action = actionOption.getName();

    return switch (action.toLowerCase()) {
      case "status" -> handleServerStatus(event, serverId);
      case "start" -> handleServerStart(event, serverId);
      case "stop" -> handleServerStop(event, serverId);
      case "restart" -> handleServerRestart(event, serverId);
      case "logs" -> handleServerLogs(event, serverId, actionOption);
      default -> event.editReply("Unknown action `" + action + "` for server `" + serverId + "`.").then();
    };
  }

  private Mono<Void> handleOverallStatus(ChatInputInteractionEvent event) {
    return gameServerService.getAllStatuses()
        .flatMap(statuses -> {
          if (statuses.isEmpty()) {
            return event.editReply("No game servers are currently configured in BotJDE.").then();
          }

          final EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
              .title("🎮 Game Server Status")
              .color(Color.BLUE);

          for (GameServerStatus status : statuses) {
            final String emoji = getEmojiForState(status.getState());
            final String details = status.getStatusDetails() != null ? status.getStatusDetails() : "N/A";
            final String health = status.getHealth() != null ? " (Health: " + status.getHealth() + ")" : "";
            embed.addField(
                emoji + " " + status.getServerName(),
                "**State:** " + status.getState() + "\n**Details:** " + details + health,
                false
            );
          }

          return event.editReply().withEmbeds(embed.build()).then();
        });
  }

  private Mono<Void> handleServerStatus(ChatInputInteractionEvent event, String serverId) {
    return gameServerService.getStatus(serverId)
        .flatMap(status -> {
          final EmbedCreateSpec embed = buildStatusEmbed(status);
          return event.editReply().withEmbeds(embed).then();
        });
  }

  private Mono<Void> handleServerStart(ChatInputInteractionEvent event, String serverId) {
    return gameServerService.start(serverId)
        .then(gameServerService.getStatus(serverId))
        .flatMap(status -> {
          final EmbedCreateSpec embed = EmbedCreateSpec.builder()
              .title("🚀 Server Started")
              .color(Color.GREEN)
              .description("Successfully sent start signal to **" + status.getServerName() + "**.")
              .addField("Current State", status.getState().name(), true)
              .addField("Details", status.getStatusDetails() != null ? status.getStatusDetails() : "Starting...", true)
              .build();
          return event.editReply().withEmbeds(embed).then();
        })
        .onErrorResume(e -> {
          log.error("Failed to start server {}: {}", serverId, e.getMessage(), e);
          return event.editReply("❌ Failed to start **" + serverId + "**: " + e.getMessage()).then();
        });
  }

  private Mono<Void> handleServerStop(ChatInputInteractionEvent event, String serverId) {
    return gameServerService.stop(serverId)
        .then(gameServerService.getStatus(serverId))
        .flatMap(status -> {
          final EmbedCreateSpec embed = EmbedCreateSpec.builder()
              .title("🛑 Server Stopped")
              .color(Color.RED)
              .description("Successfully stopped **" + status.getServerName() + "**.")
              .addField("Current State", status.getState().name(), true)
              .build();
          return event.editReply().withEmbeds(embed).then();
        })
        .onErrorResume(e -> {
          log.error("Failed to stop server {}: {}", serverId, e.getMessage(), e);
          return event.editReply("❌ Failed to stop **" + serverId + "**: " + e.getMessage()).then();
        });
  }

  private Mono<Void> handleServerRestart(ChatInputInteractionEvent event, String serverId) {
    return gameServerService.restart(serverId)
        .then(gameServerService.getStatus(serverId))
        .flatMap(status -> {
          final EmbedCreateSpec embed = EmbedCreateSpec.builder()
              .title("🔄 Server Restarted")
              .color(Color.YELLOW)
              .description("Successfully triggered restart for **" + status.getServerName() + "**.")
              .addField("Current State", status.getState().name(), true)
              .build();
          return event.editReply().withEmbeds(embed).then();
        })
        .onErrorResume(e -> {
          log.error("Failed to restart server {}: {}", serverId, e.getMessage(), e);
          return event.editReply("❌ Failed to restart **" + serverId + "**: " + e.getMessage()).then();
        });
  }

  private Mono<Void> handleServerLogs(ChatInputInteractionEvent event, String serverId, ApplicationCommandInteractionOption actionOption) {
    final int lines = actionOption.getOption("lines")
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asLong)
        .map(Long::intValue)
        .orElse(DEFAULT_LOG_LINES);

    return gameServerService.getLogs(serverId, lines)
        .flatMap(logContent -> {
          String safeLogs = logContent;
          if (safeLogs.length() > MAX_LOG_LENGTH) {
            safeLogs = "..." + safeLogs.substring(safeLogs.length() - MAX_LOG_LENGTH);
          }

          final String responseMessage = String.format("**📋 Recent Logs for `%s` (Last %d lines):**\n```text\n%s\n```", serverId, lines, safeLogs);
          return event.editReply(responseMessage).then();
        });
  }

  private EmbedCreateSpec buildStatusEmbed(GameServerStatus status) {
    final Color color = switch (status.getState()) {
      case RUNNING -> Color.GREEN;
      case RESTARTING, STARTING, STOPPING -> Color.YELLOW;
      case STOPPED, NOT_FOUND, ERROR -> Color.RED;
      default -> Color.GRAY;
    };

    return EmbedCreateSpec.builder()
        .title("🎮 " + status.getServerName() + " Status")
        .color(color)
        .addField("State", getEmojiForState(status.getState()) + " " + status.getState().name(), true)
        .addField("Details", status.getStatusDetails() != null ? status.getStatusDetails() : "N/A", true)
        .addField("Health", status.getHealth() != null ? status.getHealth() : "N/A", true)
        .build();
  }

  private String getEmojiForState(GameServerStatus.State state) {
    return switch (state) {
      case RUNNING -> "🟢";
      case RESTARTING, STARTING, STOPPING -> "🟡";
      case STOPPED -> "🔴";
      case ERROR, NOT_FOUND -> "❌";
      default -> "⚪";
    };
  }
}
