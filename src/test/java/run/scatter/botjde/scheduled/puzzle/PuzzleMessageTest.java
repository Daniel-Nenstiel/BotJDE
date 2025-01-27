package run.scatter.botjde.scheduled.puzzle;

import discord4j.common.util.Snowflake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.entity.Server;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PuzzleMessageTest {

  private PuzzleMessage puzzleMessage;
  private Server mockServer;

  @BeforeEach
  void setUp() {
    puzzleMessage = new PuzzleMessage();
    mockServer = mock(Server.class);

    // Stub default behavior
    when(mockServer.getPuzzleChannelId()).thenReturn(Snowflake.of(123456789012345678L));
    when(mockServer.isPuzzlesEnabled()).thenReturn(true);
  }

  @Test
  void generateMessages_returnsFormattedPuzzleMessage() {
    // Generate messages
    List<String> messages = puzzleMessage.generateMessages(mockServer);

    // Verify message content
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0)).contains("Good morning! Here are today’s NYTimes puzzles:");
    assertThat(messages.get(0)).contains("- [Mini Crossword](<https://www.nytimes.com/crosswords/game/mini>)");
    assertThat(messages.get(0)).contains("- [Wordle](<https://www.nytimes.com/games/wordle/index.html>)");
  }

  @Test
  void getChannelId_returnsPuzzleChannelId() {
    // Assert channel ID is correct
    assertThat(puzzleMessage.getChannelId(mockServer)).isEqualTo(Snowflake.of(123456789012345678L));
  }

  @Test
  void isEnabled_checksServerConfig() {
    // Verify when puzzles are enabled
    when(mockServer.isPuzzlesEnabled()).thenReturn(true);
    assertThat(puzzleMessage.isEnabled(mockServer)).isTrue();

    // Verify when puzzles are disabled
    when(mockServer.isPuzzlesEnabled()).thenReturn(false);
    assertThat(puzzleMessage.isEnabled(mockServer)).isFalse();
  }

  @Test
  void getDefaultPuzzleMessage_returnsStaticPuzzleMessage() {
    // Retrieve default message
    String defaultMessage = puzzleMessage.getDefaultPuzzleMessage();

    // Verify it matches the expected format
    assertThat(defaultMessage).contains("Good morning! Here are today’s NYTimes puzzles:");
    assertThat(defaultMessage).contains("- [Spelling Bee](<https://www.nytimes.com/puzzles/spelling-bee>)");
  }
}
