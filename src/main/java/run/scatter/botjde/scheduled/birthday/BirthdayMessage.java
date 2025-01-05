package run.scatter.botjde.scheduled.birthday;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import run.scatter.botjde.scheduled.ScheduledMessage;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;
import run.scatter.botjde.scheduled.entity.Birthday;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;
import run.scatter.botjde.utils.TimeUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;

@Service
public class BirthdayMessage implements ScheduledMessage {
  private static final Logger log = LoggerFactory.getLogger( ScheduledMessage.class );

  @Autowired
  private BirthdayDao birthdayDao;

  @Autowired
  Discord discordUtils;

  @Value("${defaultChannelId}")
  protected String channelId;

  @Scheduled(cron = "0 0 9 * * ?")
  @Bean
  public void checkEvent() {
    LocalDate today = LocalDateTime.now().toLocalDate();
    //Check to make sure the scheduler didn't misfire. (It does this on application startup)
    LocalDateTime nineAM = LocalDateTime.of(today, LocalTime.of(9,0));
    if(Time.isNowNearTime(nineAM, 30, TimeUnit.SECONDS)) { return; }

    birthdayDao.getBirthdays(today).forEach(birthday ->sendMessage(formatMessage(birthday)));
  }

  @Override
  public void sendMessage(String msg) {
    boolean loggedIn = false;
    GatewayDiscordClient client = null;

    try{
      client = discordUtils.getClient();
      loggedIn = true;

      client.getChannelById(Snowflake.of(channelId))
          .ofType(MessageChannel.class)
          .flatMap(channel -> channel.createMessage("happy birthday"))
          .subscribe();

    } catch (Exception err) {
      log.error("Error during scheduled message: ", err);
    }

    if(loggedIn && !isNull(client)) { client.logout(); }
  }

  protected String formatMessage() {
    return "Happy Birthday";
  }

  protected String formatMessage(Birthday birthday) {
    return "Happy Birthday " + birthday.toString();
  }
}