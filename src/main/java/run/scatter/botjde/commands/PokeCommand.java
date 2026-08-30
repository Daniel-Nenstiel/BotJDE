package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.service.PocketCommandHandler;

/**
 * /poke — Placeholder command backed by {@link PocketCommandHandler}.
 * Skeleton retained from the Pokemon TCG Pocket feature branch.
 */
@Slf4j
@Component
public class PokeCommand implements SlashCommand {

    private final PocketCommandHandler commandHandler;

    public PokeCommand(PocketCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getName() {
        return "poke";
    }

    @Override
    public ApplicationCommandRequest getCommandDefinition() {
        return ApplicationCommandRequest.builder()
            .name(getName())
            .description("Poke the bot")
            .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        final String author = event.getInteraction().getUser().getUsername();
        final String response = commandHandler.handleCommand(getName(), author);
        return event.reply(response).then();
    }
}
