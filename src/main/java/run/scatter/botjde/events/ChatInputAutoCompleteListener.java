package run.scatter.botjde.events;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.commands.SlashCommand;

import java.util.List;

/**
 * Listens for Discord autocomplete interactions and dispatches them to the
 * appropriate {@link SlashCommand} handler.
 */
@Slf4j
@Service
public class ChatInputAutoCompleteListener implements EventListener<ChatInputAutoCompleteEvent> {

  private final List<SlashCommand> slashCommands;

  public ChatInputAutoCompleteListener(List<SlashCommand> slashCommands) {
    this.slashCommands = slashCommands;
  }

  @Override
  public Class<ChatInputAutoCompleteEvent> getEventType() {
    return ChatInputAutoCompleteEvent.class;
  }

  @Override
  public Mono<Void> execute(ChatInputAutoCompleteEvent event) {
    return slashCommands.stream()
        .filter(command -> command.getName().equals(event.getCommandName()))
        .findFirst()
        .map(command -> command.handleAutocomplete(event))
        .orElseGet(Mono::empty);
  }
}
