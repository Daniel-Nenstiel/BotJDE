package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import run.scatter.botjde.entity.server.Server;

import java.util.List;

public abstract class BaseScheduledMessage implements ScheduledMessage {

  @Override
  public List<String> checkEvent(Server server) {
    if (!isEnabled(server)) {
      return List.of();
    }
    return generateMessages(server);
  }

  protected abstract boolean isEnabled(Server server);
  protected abstract List<String> generateMessages(Server server);

  public Snowflake getChannelId(Server server) {
    return server.getDefaultChannelId();
  }
}
