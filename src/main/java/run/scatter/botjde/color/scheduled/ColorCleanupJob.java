package run.scatter.botjde.color.scheduled;

import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.service.ColorService;
import run.scatter.botjde.scheduled.tasks.ScheduledTask;

/**
 * Scheduled job that periodically sweeps all guilds for unused (orphan) color roles and deletes them.
 */
@Slf4j
@Component
public class ColorCleanupJob implements ScheduledTask {

  private static final String CRON_DAILY_4AM = "0 0 4 * * ?";

  private final ColorService colorService;
  private final GatewayDiscordClient gatewayDiscordClient;

  @Autowired
  public ColorCleanupJob(ColorService colorService, @Lazy @Autowired(required = false) GatewayDiscordClient gatewayDiscordClient) {
    this.colorService = colorService;
    this.gatewayDiscordClient = gatewayDiscordClient;
  }

  @Override
  public String getName() {
    return "colorCleanup";
  }

  @Override
  public String getCronExpression() {
    return CRON_DAILY_4AM;
  }

  @Override
  public void execute() {
    if (gatewayDiscordClient == null) {
      log.warn("GatewayDiscordClient not available, skipping scheduled color cleanup job.");
      return;
    }

    gatewayDiscordClient.getGuilds()
        .flatMap(guild -> colorService.cleanupOrphanColorRoles(guild)
            .doOnNext(count -> {
              if (count > 0) {
                log.info("Cleaned up {} unused color role(s) in guild '{}'", count, guild.getName());
              } else {
                log.info("No unused color roles found in guild '{}'", guild.getName());
              }
            }))
        .onErrorResume(e -> {
          log.error("Error during scheduled color cleanup job: {}", e.getMessage(), e);
          return Mono.empty();
        })
        .subscribe();
  }
}
