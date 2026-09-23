package run.scatter.botjde.scheduled.anniversary;

import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.persistence.anniversary.dao.AnniversaryDao;
import run.scatter.botjde.scheduled.BaseScheduledMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Scheduled task that checks for anniversaries and sends celebration messages daily at 9:00 AM.
 */
@Slf4j
@Component
public class AnniversaryMessage extends BaseScheduledMessage {

  private static final String CRON_DAILY_9AM = "0 0 9 * * ?";

  private final AnniversaryDao anniversaryDao;

  @Autowired
  public AnniversaryMessage(
      AnniversaryDao anniversaryDao,
      AppConfig appConfig,
      @Lazy @Autowired(required = false) GatewayDiscordClient gatewayDiscordClient
  ) {
    super(appConfig, gatewayDiscordClient);
    this.anniversaryDao = anniversaryDao;
  }

  @Override
  public String getName() {
    return "anniversaries";
  }

  @Override
  public String getCronExpression() {
    return CRON_DAILY_9AM;
  }

  @Override
  protected boolean isEnabled(Server server) {
    return server.isAnniversariesEnabled();
  }

  @Override
  protected List<String> generateMessages(Server server) {
    final List<Anniversary> anniversariesToday = anniversaryDao.getTodaysAnniversariesForServer(server.getId().asString());
    if (anniversariesToday.isEmpty()) {
      log.info("No anniversaries today for server: {}", server != null ? server.getName() : "N/A");
      return List.of();
    }
    return anniversariesToday.stream()
        .map(this::formatMessage)
        .collect(Collectors.toList());
  }

  private String formatMessage(Anniversary anniversary) {
    if (anniversary == null || anniversary.getFormattedNames() == null) {
      return "Happy Anniversary!";
    }
    return String.format("Happy Anniversary to %s!", anniversary.getFormattedNames());
  }
}
