package run.scatter.botjde.scheduled;

import run.scatter.botjde.config.AppConfig;

import java.util.List;

public interface ScheduledMessage {
  String getType(); // Returns the message type (e.g., "birthdays", "puzzles")
  List<String> checkEvent(AppConfig.Server server);
}
