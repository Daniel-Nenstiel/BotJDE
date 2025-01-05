package run.scatter.botjde.scheduled;

import org.springframework.stereotype.Component;

@Component
public interface ScheduledMessage {
  void sendMessage(String msg);
}