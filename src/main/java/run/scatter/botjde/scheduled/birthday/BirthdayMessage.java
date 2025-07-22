package run.scatter.botjde.scheduled.birthday;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.entity.server.Server;
import run.scatter.botjde.scheduled.BaseScheduledMessage;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("birthdays")
public class BirthdayMessage extends BaseScheduledMessage {
  private final BirthdayDao birthdayDao;

  public BirthdayMessage(BirthdayDao birthdayDao) {
    this.birthdayDao = birthdayDao;
  }

  @Override
  public String getType() {
    return "birthdays";
  }

  @Override
  protected boolean isEnabled(Server server) {
    return server.isBirthdaysEnabled();
  }

  @Override
  protected List<String> generateMessages(Server server) {
    List<Birthday> birthdaysToday = birthdayDao.getTodaysBirthdaysForServer(server.getId().asString());
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
