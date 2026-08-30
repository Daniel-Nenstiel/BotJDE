package run.scatter.botjde.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;

import java.util.List;

/**
 * Registers all {@link SlashCommand} beans with Discord as guild application commands
 * at application startup. Guild commands propagate instantly (unlike global commands
 * which can take up to 1 hour). Commands are registered for every server defined in
 * {@link AppConfig}.
 */
@Slf4j
@Component
public class SlashCommandRegistrar implements ApplicationListener<ApplicationReadyEvent> {

    private final GatewayDiscordClient client;
    private final AppConfig appConfig;
    private final List<SlashCommand> slashCommands;

    public SlashCommandRegistrar(GatewayDiscordClient client, AppConfig appConfig, List<SlashCommand> slashCommands) {
        this.client = client;
        this.appConfig = appConfig;
        this.slashCommands = slashCommands;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (client == null) {
            log.error("GatewayDiscordClient is null — skipping slash command registration.");
            return;
        }

        final Long applicationId = client.getRestClient().getApplicationId().block();

        if (applicationId == null) {
            log.error("Could not retrieve Discord application ID — skipping slash command registration.");
            return;
        }

        final List<ApplicationCommandRequest> commandRequests = slashCommands.stream()
            .map(SlashCommand::getCommandDefinition)
            .toList();

        log.info("Registering {} slash command(s): {}",
            commandRequests.size(),
            slashCommands.stream().map(SlashCommand::getName).toList()
        );

        for (Server server : appConfig.getServers()) {
            final long guildId = server.getId().asLong();
            for (ApplicationCommandRequest request : commandRequests) {
                client.getRestClient()
                    .getApplicationService()
                    .createGuildApplicationCommand(applicationId, guildId, request)
                    .doOnSuccess(r -> log.info("Registered '{}' for server '{}'", request.name(), server.getName()))
                    .doOnError(e -> log.error("Failed to register '{}' for server '{}': {}", request.name(), server.getName(), e.getMessage()))
                    .subscribe();
            }
        }
    }
}
