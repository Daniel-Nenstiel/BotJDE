package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.server.Server;
import run.scatter.botjde.utils.Discord;

import java.util.List;

import static org.mockito.Mockito.*;

class MessageOrchestratorTest {

  private AppConfig mockAppConfig;
  private ScheduledMessage mockBirthdayHandler;
  private ScheduledMessage mockPuzzleHandler;
  private Discord mockDiscordUtils;
  private MessageOrchestrator orchestrator;

  @BeforeEach
  void setUp() {
    // Create mocks
    mockAppConfig = mock(AppConfig.class);
    mockDiscordUtils = mock(Discord.class);
    mockBirthdayHandler = mock(ScheduledMessage.class);
    mockPuzzleHandler = mock(ScheduledMessage.class);

    // Stub unique handler types
    when(mockBirthdayHandler.getType()).thenReturn("birthdays");
    when(mockPuzzleHandler.getType()).thenReturn("puzzles");

    // Create a list of handlers
    List<ScheduledMessage> handlers = List.of(mockBirthdayHandler, mockPuzzleHandler);

    // Initialize orchestrator with the mocked List
    orchestrator = new MessageOrchestrator(handlers, mockAppConfig, mockDiscordUtils);
  }

  @Test
  void handleMessages_shouldInvokeHandlers() {
    // Mock server configuration
    Server mockServer = mock(Server.class);
    when(mockServer.getName()).thenReturn("Test Server");
    when(mockServer.getDefaultChannelId()).thenReturn(Snowflake.of(12345L));

    // Return mock server in app config
    when(mockAppConfig.getServers()).thenReturn(List.of(mockServer));

    // Return messages from the handler
    when(mockBirthdayHandler.checkEvent(mockServer)).thenReturn(List.of("Happy Birthday!"));

    // Call handleMessages directly
    orchestrator.handleMessages("birthdays");

    // Verify that the handler was invoked
    verify(mockBirthdayHandler, times(1)).checkEvent(mockServer);

    // Verify no interaction with the puzzle handler
    verify(mockPuzzleHandler, never()).checkEvent(any());
  }

  @Test
  void handleMessages_shouldSkipIfNoServersHaveMessages() {
    // Mock server configuration
    Server mockServer = mock(Server.class);
    when(mockAppConfig.getServers()).thenReturn(List.of(mockServer));

    // Return no messages from the handler
    when(mockBirthdayHandler.checkEvent(mockServer)).thenReturn(List.of());

    // Call handleMessages
    orchestrator.handleMessages("birthdays");

    // Verify the handler was invoked but no messages were returned
    verify(mockBirthdayHandler, times(1)).checkEvent(mockServer);

    // No messages mean no further processing
    verify(mockDiscordUtils, never()).getClient();
    verify(mockPuzzleHandler, never()).checkEvent(any());
  }

  @Test
  void handleMessages_shouldLogErrorForInvalidMessageType() {
    // Call handleMessages with an invalid type
    orchestrator.handleMessages("invalidType");

    // Verify no interactions with any handler
    verify(mockBirthdayHandler, never()).checkEvent(any());
    verify(mockPuzzleHandler, never()).checkEvent(any());
  }
}
