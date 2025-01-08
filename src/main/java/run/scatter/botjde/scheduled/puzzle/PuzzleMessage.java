package run.scatter.botjde.scheduled.puzzles;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.scatter.botjde.scheduled.ScheduledMessage;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
public class PuzzleMessage implements ScheduledMessage {

  private static final Logger log = LoggerFactory.getLogger(PuzzleMessage.class);

  @Autowired
  private Discord discordUtils;

  @Value("${puzzleChannelId:}")
  private String puzzleChannelId;

//  @Scheduled(cron = "*/10 * * * * ?")
  @Scheduled(cron = "0 0 6 * * ?") // 6:00 AM daily
  public void sendDailyPuzzles() {
    if (puzzleChannelId == null || puzzleChannelId.isEmpty()) {
      log.error("Channel ID is not configured. Cannot send messages.");
      return;
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sixAM = now.toLocalDate().atTime(LocalTime.of(6, 0));

    // Ensure we proceed only if the current time is close to 6:00 AM
    if (!Time.isNowNearTime(sixAM, 30, ChronoUnit.SECONDS)) {
      log.info("Skipping scheduled task because it's not near 6:00 AM.");
      return;
    }

    // Send the message
    try {
      sendMessage(formatMessage());
    } catch (Exception ex) {
      log.error("Error while sending puzzle message: ", ex);
    }
  }

  @Override
  public void sendMessage(String msg) {
    if (msg == null || msg.isEmpty()) {
      log.warn("Attempted to send an empty message. Skipping.");
      return;
    }

    GatewayDiscordClient client = null;
    boolean loggedIn = false;

    try {
      client = discordUtils.getClient();
      loggedIn = true;

      client.getChannelById(Snowflake.of(puzzleChannelId))
          .ofType(GuildMessageChannel.class)
          .flatMap(thread -> thread.createMessage(msg))
          .subscribe();

      log.info("Successfully sent puzzle message: {}", msg);

    } catch (Exception err) {
      log.error("Error during scheduled puzzle message: ", err);
    } finally {
      if (loggedIn && client != null) {
        try {
          client.logout().block();
          log.info("Discord client logged out successfully.");
        } catch (Exception ex) {
          log.warn("Failed to log out Discord client: ", ex);
        }
      }
    }
  }

  public String formatMessage() {
    return """
        Good morning! Here are today’s NYTimes puzzles:
        
        - [Mini Crossword](<https://www.nytimes.com/crosswords/game/mini>)
        - [Wordle](<https://www.nytimes.com/games/wordle/index.html>)
        - [Spelling Bee](<https://www.nytimes.com/puzzles/spelling-bee>)
        - [Crossword](<https://www.nytimes.com/crosswords/game/daily>)
        
        """;
  }
}
