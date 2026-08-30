package run.scatter.botjde.events;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import run.scatter.botjde.commands.SlashCommand;

import java.util.List;

import static org.mockito.Mockito.*;

class SlashCommandListenerTest {

  private SlashCommand mockCommand;
  private ChatInputInteractionEvent mockEvent;
  private SlashCommandListener listener;

  @BeforeEach
  void setUp() {
    mockCommand = mock(SlashCommand.class);
    mockEvent = mock(ChatInputInteractionEvent.class);
    listener = new SlashCommandListener(List.of(mockCommand));
  }

  @Test
  void execute_withMatchingCommand_callsHandler() {
    when(mockCommand.getName()).thenReturn("test");
    when(mockEvent.getCommandName()).thenReturn("test");
    when(mockCommand.handle(mockEvent)).thenReturn(Mono.empty());

    listener.execute(mockEvent).block();

    verify(mockCommand, times(1)).handle(mockEvent);
  }

  @Test
  void execute_withUnknownCommand_returnsEmptyWithoutCallingHandler() {
    when(mockCommand.getName()).thenReturn("test");
    when(mockEvent.getCommandName()).thenReturn("unknown");

    listener.execute(mockEvent).block();

    verify(mockCommand, never()).handle(any());
  }

  @Test
  void execute_withMultipleCommands_callsOnlyMatchingHandler() {
    SlashCommand mockCommand2 = mock(SlashCommand.class);
    SlashCommandListener multiListener = new SlashCommandListener(List.of(mockCommand, mockCommand2));

    when(mockCommand.getName()).thenReturn("test");
    when(mockCommand2.getName()).thenReturn("puzzle");
    when(mockEvent.getCommandName()).thenReturn("puzzle");
    when(mockCommand2.handle(mockEvent)).thenReturn(Mono.empty());

    multiListener.execute(mockEvent).block();

    verify(mockCommand, never()).handle(any());
    verify(mockCommand2, times(1)).handle(mockEvent);
  }

  @Test
  void getEventType_returnsChatInputInteractionEvent() {
    assert listener.getEventType() == ChatInputInteractionEvent.class;
  }
}
