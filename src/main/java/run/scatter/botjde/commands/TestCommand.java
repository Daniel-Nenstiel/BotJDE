package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * /test — Pings the bot. Fires log statements at all levels for observability testing.
 */
@Slf4j
@Component
public class TestCommand implements SlashCommand {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public ApplicationCommandRequest getCommandDefinition() {
        return ApplicationCommandRequest.builder()
            .name(getName())
            .description("Ping the bot to verify it is running")
            .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        log.info("Info Log Ping");
        log.trace("Trace log ping");
        log.warn("Warn Log Ping");
        log.debug("Debug Log Ping");
        log.error("Error log ping");
        return event.reply("pong!").then();
    }
}
