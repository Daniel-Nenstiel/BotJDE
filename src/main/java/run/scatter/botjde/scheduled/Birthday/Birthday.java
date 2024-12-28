import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;
import discord4j.common.util.Snowflake;
import discord

public class Birthday implements Schedule {
  @Override
  @Scheduled(cron = "* * * * *")
  public Void message() {
    client.getChannelById(Snowflake.of("${defaultChannelId}"))
      .flatMap(channel -> channel.createMessage("Happy Birthday!"))
      .subscribe();
  }
}