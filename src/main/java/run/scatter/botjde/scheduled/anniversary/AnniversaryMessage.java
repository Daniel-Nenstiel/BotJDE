package run.scatter.botjde.scheduled.anniversary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.entity.server.Server;
import run.scatter.botjde.scheduled.BaseScheduledMessage;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("anniversaries")
public class AnniversaryMessage extends BaseScheduledMessage {
  private final AnniversaryDao anniversaryDao;

  public AnniversaryMessage(AnniversaryDao anniversaryDao) {
    this.anniversaryDao = anniversaryDao;
  }

  @Override
  public String getType() {
    return "anniversaries";
  }

  @Override
  protected boolean isEnabled(Server server) {
    return server.isAnniversariesEnabled();
  }

  @Override
  protected List<String> generateMessages(Server server) {
    List<Anniversary> anniversariesToday = anniversaryDao.getTodaysAnniversaries();
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
