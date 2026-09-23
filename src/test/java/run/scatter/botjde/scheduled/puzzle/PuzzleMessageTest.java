package run.scatter.botjde.scheduled.puzzle;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PuzzleMessageTest {

  private AppConfig appConfig;
  private GatewayDiscordClient client;
  private PuzzleMessage puzzleMessage;
  private Server mockServer;

  @BeforeEach
  void setUp() {
    appConfig = mock(AppConfig.class);
    client = mock(GatewayDiscordClient.class);
    puzzleMessage = new PuzzleMessage(appConfig, client);
    mockServer = mock(Server.class);

    when(mockServer.getPuzzleChannelId()).thenReturn(Snowflake.of(123456789012345678L));
    when(mockServer.isPuzzlesEnabled()).thenReturn(true);
  }

  @Test
  void taskMetadata_returnsExpectedValues() {
    assertThat(puzzleMessage.getName()).isEqualTo("puzzles");
    assertThat(puzzleMessage.getCronExpression()).isEqualTo("0 0 6 * * ?");
  }

  @Test
  void generateMessages_returnsFormattedPuzzleMessage() {
    List<String> messages = puzzleMessage.generateMessages(mockServer);

    assertThat(messages).hasSize(1);
    assertThat(messages.get(0)).contains("Good morning! Here are today’s NYTimes puzzles:");
    assertThat(messages.get(0)).contains("- [Mini Crossword](<https://www.nytimes.com/crosswords/game/mini>)");
    assertThat(messages.get(0)).contains("- [Wordle](<https://www.nytimes.com/games/wordle/index.html>)");
  }

  @Test
  void getChannelId_returnsPuzzleChannelId() {
    assertThat(puzzleMessage.getChannelId(mockServer)).isEqualTo(Snowflake.of(123456789012345678L));
  }

  @Test
  void isEnabled_checksServerConfig() {
    when(mockServer.isPuzzlesEnabled()).thenReturn(true);
    assertThat(puzzleMessage.isEnabled(mockServer)).isTrue();

    when(mockServer.isPuzzlesEnabled()).thenReturn(false);
    assertThat(puzzleMessage.isEnabled(mockServer)).isFalse();
  }

  @Test
  void getDefaultPuzzleMessage_returnsStaticPuzzleMessage() {
    String defaultMessage = puzzleMessage.getDefaultPuzzleMessage();

    assertThat(defaultMessage).contains("Good morning! Here are today’s NYTimes puzzles:");
    assertThat(defaultMessage).contains("- [Mini Crossword](<https://www.nytimes.com/crosswords/game/mini>)");
  }
}
