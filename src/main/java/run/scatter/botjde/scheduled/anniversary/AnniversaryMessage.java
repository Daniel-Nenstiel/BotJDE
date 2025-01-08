package run.scatter.botjde.scheduled.anniversary;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.scheduled.ScheduledMessage;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
public class AnniversaryMessage implements ScheduledMessage {
  private static final Logger log = LoggerFactory.getLogger(AnniversaryMessage.class);

  @Autowired
  private AnniversaryDao anniversaryDao;

  @Autowired
  private Discord discordUtils;

  @Value("${defaultChannelId:}")
  private String channelId;

  @Scheduled(cron = "0 0 9 * * ?")
  public void checkEvent() {
    if (channelId == null || channelId.isEmpty()) {
      log.error("Channel ID is not configured. Cannot send messages.");
      return;
    }

    LocalDate today = LocalDateTime.now().toLocalDate();
    LocalDateTime nineAM = LocalDateTime.of(today, LocalTime.of(9, 0));

    // Ensure we proceed only if the current time is NOT near 9:00 AM
    if (!Time.isNowNearTime(nineAM, 30, ChronoUnit.SECONDS)) {
      log.info("Skipping scheduled task because it's not near 9:00 AM.");
      return;
    }

    // Fetch and process anniversaries
    try {
      anniversaryDao.getTodaysAnniversaries().forEach(anniversary -> {
        String message = formatMessage(anniversary);
        sendMessage(message);
      });
    } catch (Exception ex) {
      log.error("Error while processing anniversaries: ", ex);
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

      client.getChannelById(Snowflake.of(channelId))
          .ofType(MessageChannel.class)
          .flatMap(channel -> channel.createMessage(msg))
          .subscribe();

      log.info("Successfully sent message: {}", msg);

    } catch (Exception err) {
      log.error("Error during scheduled message: ", err);
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
    return "Happy Anniversary!";
  }

  public String formatMessage(Anniversary anniversary) {
    if (anniversary == null || anniversary.getFormattedNames() == null) {
      return "Happy Anniversary!";
    }
    return String.format("Happy Anniversary to %s!", anniversary.getFormattedNames());
  }
}
