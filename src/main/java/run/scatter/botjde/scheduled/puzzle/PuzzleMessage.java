package run.scatter.botjde.scheduled.puzzle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.scheduled.BaseScheduledMessage;

import java.util.List;

@Service
public class PuzzleMessage extends BaseScheduledMessage {

  private static final Logger log = LoggerFactory.getLogger(PuzzleMessage.class);

  @Override
  public String getType() {
    return "puzzles";
  }

  @Override
  protected boolean isEnabled(AppConfig.Server server) {
    return server.isPuzzlesEnabled();
  }

  @Override
  protected List<String> generateMessages(AppConfig.Server server) {
    return List.of(formatMessage());
  }

  private String formatMessage() {
    return """
            Good morning! Here are today’s NYTimes puzzles:

            - [Mini Crossword](<https://www.nytimes.com/crosswords/game/mini>)
            - [Wordle](<https://www.nytimes.com/games/wordle/index.html>)
            - [Spelling Bee](<https://www.nytimes.com/puzzles/spelling-bee>)
            - [Crossword](<https://www.nytimes.com/crosswords/game/daily>)
            """;
  }

  public String getDefaultPuzzleMessage() {
    return generateMessages(null).get(0); // Always returns the first (and only) default message
  }
}
