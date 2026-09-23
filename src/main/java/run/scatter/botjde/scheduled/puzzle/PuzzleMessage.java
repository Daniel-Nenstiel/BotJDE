package run.scatter.botjde.scheduled.puzzle;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.scheduled.BaseScheduledMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scheduled task that posts daily NYTimes puzzle links at 6:00 AM.
 */
@Slf4j
@Component
public class PuzzleMessage extends BaseScheduledMessage {

  private static final String CRON_DAILY_6AM = "0 0 6 * * ?";

  private static final Map<String, String> PUZZLES = Map.of(
      "Wordle", "https://www.nytimes.com/games/wordle/index.html",
      "Mini Crossword", "https://www.nytimes.com/crosswords/game/mini",
      "Connections", "https://www.nytimes.com/games/connections",
      "Strands", "https://www.nytimes.com/games/strands",
      "TriviaV", "https://triviav.com/"
  );

  @Autowired
  public PuzzleMessage(
      AppConfig appConfig,
      @Lazy @Autowired(required = false) GatewayDiscordClient gatewayDiscordClient
  ) {
    super(appConfig, gatewayDiscordClient);
  }

  @Override
  public String getName() {
    return "puzzles";
  }

  @Override
  public String getCronExpression() {
    return CRON_DAILY_6AM;
  }

  @Override
  protected boolean isEnabled(Server server) {
    return server.isPuzzlesEnabled();
  }

  @Override
  protected List<String> generateMessages(Server server) {
    return List.of(formatMessage());
  }

  private String formatMessage() {
    final String puzzleLinks = PUZZLES.entrySet().stream()
        .map(entry -> String.format("- [%s](<%s>)", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining("\n"));

    return String.format("Good morning! Here are today’s NYTimes puzzles:\n\n%s", puzzleLinks);
  }

  public String getDefaultPuzzleMessage() {
    return formatMessage();
  }

  @Override
  public Snowflake getChannelId(Server server) {
    return server.getPuzzleChannelId();
  }
}
