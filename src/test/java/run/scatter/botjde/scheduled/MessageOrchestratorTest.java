package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.config.AppConfig.Server;
import run.scatter.botjde.utils.Discord;

import java.util.List;

import static org.mockito.Mockito.*;

class MessageOrchestratorTest {

  private AppConfig mockAppConfig;
  private ScheduledMessage mockBirthdayHandler;
  private MessageOrchestrator orchestrator;

  @BeforeEach
  void setUp() {
    // Create mocks
    mockAppConfig = mock(AppConfig.class);
    Discord mockDiscordUtils = mock(Discord.class);
    mockBirthdayHandler = mock(ScheduledMessage.class);
    ScheduledMessage mockPuzzleHandler = mock(ScheduledMessage.class);

    // Stub unique handler types
    when(mockBirthdayHandler.getType()).thenReturn("birthdays");
    when(mockPuzzleHandler.getType()).thenReturn("puzzles");

    // Initialize orchestrator with mocked dependencies
    orchestrator = new MessageOrchestrator(
        List.of(mockBirthdayHandler, mockPuzzleHandler),
        mockAppConfig,
        mockDiscordUtils
    );
  }

  @Test
  void handleMessages_shouldInvokeHandlers() {
    // Mock server configuration
    Server mockServer = mock(Server.class);
    when(mockServer.getName()).thenReturn("Test Server");
    when(mockServer.getDefaultChannelId()).thenReturn(Snowflake.of(12345L));

    when(mockAppConfig.getServers()).thenReturn(List.of(mockServer));
    when(mockBirthdayHandler.checkEvent(mockServer)).thenReturn(List.of("Happy Birthday!"));

    // Call handleMessages directly
    orchestrator.handleMessages("birthdays");

    // Verify interaction
    verify(mockBirthdayHandler, times(1)).checkEvent(mockServer);
  }
}