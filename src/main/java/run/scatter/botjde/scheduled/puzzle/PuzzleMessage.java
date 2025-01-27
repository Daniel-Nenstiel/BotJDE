package run.scatter.botjde.scheduled.puzzle;

import discord4j.common.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.scheduled.BaseScheduledMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("puzzles")
public class PuzzleMessage extends BaseScheduledMessage {
  private static final Map<String, String> PUZZLES = Map.of(
      "Mini Crossword", "https://www.nytimes.com/crosswords/game/mini",
      "Wordle", "https://www.nytimes.com/games/wordle/index.html",
      "Spelling Bee", "https://www.nytimes.com/puzzles/spelling-bee",
      "Crossword", "https://www.nytimes.com/crosswords/game/daily"
  );

  @Override
  public String getType() {
    return "puzzles";
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
    String puzzleLinks = PUZZLES.entrySet().stream()
        .map(entry -> String.format("- [%s](<%s>)", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining("\n"));

    return String.format("Good morning! Here are today’s NYTimes puzzles:\n\n%s", puzzleLinks);
  }

  public String getDefaultPuzzleMessage() {
    return formatMessage(); // Directly call the formatting method
  }

  @Override
  public Snowflake getChannelId(Server server) {
    return server.getPuzzleChannelId();
  }
}
