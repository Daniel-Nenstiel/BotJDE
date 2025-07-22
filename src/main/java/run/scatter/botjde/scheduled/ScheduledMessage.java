package run.scatter.botjde.scheduled;

import discord4j.common.util.Snowflake;
import run.scatter.botjde.entity.Server;

import java.util.List;

public interface ScheduledMessage {
  String getType();
  List<String> checkEvent(Server server);
  Snowflake getChannelId(Server server);
}
