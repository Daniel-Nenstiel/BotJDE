package run.scatter.botjde.scheduled.models.impl;

import run.scatter.botjde.scheduled.models.DatedEvent;

import java.time.LocalDateTime;

public class Birthday implements DatedEvent {

  private LocalDateTime eventDate;

  private String actor;

  @Override
  public String getFormattedActors() {
    return actor;
  }

  @Override
  public LocalDateTime getEventDate() {
    return eventDate;
  }
}
