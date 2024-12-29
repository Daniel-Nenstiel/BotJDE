package run.scatter.botjde.scheduled.Birthday;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import run.scatter.botjde.BotConfiguration;
import run.scatter.botjde.scheduled.Schedule;
import run.scatter.botjde.utils.Discord;
import run.scatter.botjde.utils.Time;
import run.scatter.botjde.utils.TimeUnit;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;

@Service
public class Birthday implements Schedule {
  private static final Logger log = LoggerFactory.getLogger( Schedule.class );

  @Autowired
  Discord discordUtils;

  @Value("${defaultChannelId}")
  private String channelId;

  final String CRON_EXPRESSION = "";
//  final String CRON_EXPRESSION = "*/30 * * * * *";

  @Override
  @Scheduled(cron = "0 0 9 * * ?")
  @Bean
  public void message() {
    //Check to make sure the scheduler didn't misfire. (It does this on application startup)
    LocalDateTime nineAM = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(9,0));
    if(Time.isNowNearTime(nineAM, 30, TimeUnit.SECONDS)) { return; }


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
}