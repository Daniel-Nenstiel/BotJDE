package run.scatter.botjde.events;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import run.scatter.botjde.commands.SlashCommand;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChatInputAutoCompleteListenerTest {

  private SlashCommand mockCommand;
  private ChatInputAutoCompleteListener listener;

  @BeforeEach
  void setUp() {
    mockCommand = mock(SlashCommand.class);
    when(mockCommand.getName()).thenReturn("color");
    listener = new ChatInputAutoCompleteListener(List.of(mockCommand));
  }

  @Test
  void getEventType_returnsChatInputAutoCompleteEvent() {
    assertThat(listener.getEventType()).isEqualTo(ChatInputAutoCompleteEvent.class);
  }

  @Test
  void execute_dispatchesToMatchingCommand() {
    ChatInputAutoCompleteEvent mockEvent = mock(ChatInputAutoCompleteEvent.class);
    when(mockEvent.getCommandName()).thenReturn("color");
    when(mockCommand.handleAutocomplete(mockEvent)).thenReturn(Mono.empty());

    listener.execute(mockEvent).block();

    verify(mockCommand, times(1)).handleAutocomplete(mockEvent);
  }

  @Test
  void execute_ignoresUnknownCommand() {
    ChatInputAutoCompleteEvent mockEvent = mock(ChatInputAutoCompleteEvent.class);
    when(mockEvent.getCommandName()).thenReturn("unknown");

    listener.execute(mockEvent).block();

    verify(mockCommand, never()).handleAutocomplete(any());
  }
}
