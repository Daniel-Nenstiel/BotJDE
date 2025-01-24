package run.scatter.botjde.scheduled;

import run.scatter.botjde.config.AppConfig;

import java.util.List;

public abstract class BaseScheduledMessage implements ScheduledMessage {

  @Override
  public List<String> checkEvent(AppConfig.Server server) {
    if (!isEnabled(server)) {
      return List.of();
    }
    return generateMessages(server);
  }

  protected abstract boolean isEnabled(AppConfig.Server server);
  protected abstract List<String> generateMessages(AppConfig.Server server);
}
