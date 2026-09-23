package run.scatter.botjde.scheduled.birthday;

import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.Server;
import run.scatter.botjde.persistence.birthday.dao.BirthdayDao;
import run.scatter.botjde.scheduled.BaseScheduledMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Scheduled task that checks for birthdays and sends celebration messages daily at 9:00 AM.
 */
@Slf4j
@Component
public class BirthdayMessage extends BaseScheduledMessage {

  private static final String CRON_DAILY_9AM = "0 0 9 * * ?";

  private final BirthdayDao birthdayDao;

  @Autowired
  public BirthdayMessage(
      BirthdayDao birthdayDao,
      AppConfig appConfig,
      @Lazy @Autowired(required = false) GatewayDiscordClient gatewayDiscordClient
  ) {
    super(appConfig, gatewayDiscordClient);
    this.birthdayDao = birthdayDao;
  }

  @Override
  public String getName() {
    return "birthdays";
  }

  @Override
  public String getCronExpression() {
    return CRON_DAILY_9AM;
  }

  @Override
  protected boolean isEnabled(Server server) {
    return server.isBirthdaysEnabled();
  }

  @Override
  protected List<String> generateMessages(Server server) {
    final List<Birthday> birthdaysToday = birthdayDao.getTodaysBirthdaysForServer(server.getId().asString());
    if (birthdaysToday.isEmpty()) {
      log.info("No birthdays today for server: {}", server != null ? server.getName() : "N/A");
      return List.of();
    }
    return birthdaysToday.stream()
        .map(this::formatMessage)
        .collect(Collectors.toList());
  }

  private String formatMessage(Birthday birthday) {
    return String.format("Happy Birthday %s!", birthday.getUser().getName());
  }
}
