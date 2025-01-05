package run.scatter.botjde.scheduled.models;

import java.time.LocalDateTime;

public interface DatedEvent {

  LocalDateTime getEventDate();

  String getFormattedActors();
}
