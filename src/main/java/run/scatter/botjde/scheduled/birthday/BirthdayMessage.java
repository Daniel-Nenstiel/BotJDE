package run.scatter.botjde.scheduled.birthday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.scheduled.BaseScheduledMessage;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BirthdayMessage extends BaseScheduledMessage {

  private static final Logger log = LoggerFactory.getLogger(BirthdayMessage.class);

  private final BirthdayDao birthdayDao;

  public BirthdayMessage(BirthdayDao birthdayDao) {
    this.birthdayDao = birthdayDao;
  }

  @Override
  public String getType() {
    return "birthdays";
  }

  @Override
  protected boolean isEnabled(AppConfig.Server server) {
    return server.isBirthdaysEnabled();
  }

  @Override
  protected List<String> generateMessages(AppConfig.Server server) {
    List<Birthday> birthdaysToday = birthdayDao.getTodaysBirthdays();
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
