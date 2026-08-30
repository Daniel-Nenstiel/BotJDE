package run.scatter.botjde.events;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.commands.SlashCommand;

import java.util.List;

/**
 * Listens for Discord slash command interactions and dispatches them to the
 * appropriate {@link SlashCommand} handler by matching the command name.
 */
@Slf4j
@Service
public class SlashCommandListener implements EventListener<ChatInputInteractionEvent> {

    private final List<SlashCommand> slashCommands;

    public SlashCommandListener(List<SlashCommand> slashCommands) {
        this.slashCommands = slashCommands;
    }

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return slashCommands.stream()
            .filter(command -> command.getName().equals(event.getCommandName()))
            .findFirst()
            .map(command -> command.handle(event))
            .orElseGet(() -> {
                log.warn("No handler found for slash command: /{}", event.getCommandName());
                return Mono.empty();
            });
    }
}
