package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.scheduled.puzzle.PuzzleMessage;

/**
 * /puzzle — Replies with today's NYTimes puzzle links on demand.
 * Reuses the same message format as the 6am scheduled post.
 */
@Slf4j
@Component
public class PuzzleCommand implements SlashCommand {

    private final PuzzleMessage puzzleMessage;

    public PuzzleCommand(PuzzleMessage puzzleMessage) {
        this.puzzleMessage = puzzleMessage;
    }

    @Override
    public String getName() {
        return "puzzle";
    }

    @Override
    public ApplicationCommandRequest getCommandDefinition() {
        return ApplicationCommandRequest.builder()
            .name(getName())
            .description("Get today's NYTimes puzzle links")
            .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        log.info("Puzzle command called by {}", event.getInteraction().getUser().getUsername());
        return event.reply(puzzleMessage.getDefaultPuzzleMessage()).then();
    }
}
