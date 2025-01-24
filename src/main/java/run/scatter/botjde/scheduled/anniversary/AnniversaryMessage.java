package run.scatter.botjde.scheduled.anniversary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.scheduled.BaseScheduledMessage;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnniversaryMessage extends BaseScheduledMessage {

  private static final Logger log = LoggerFactory.getLogger(AnniversaryMessage.class);

  private final AnniversaryDao anniversaryDao;

  public AnniversaryMessage(AnniversaryDao anniversaryDao) {
    this.anniversaryDao = anniversaryDao;
  }

  @Override
  public String getType() {
    return "anniversaries";
  }

  @Override
  protected boolean isEnabled(AppConfig.Server server) {
    return server.isAnniversariesEnabled();
  }

  @Override
  protected List<String> generateMessages(AppConfig.Server server) {
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
