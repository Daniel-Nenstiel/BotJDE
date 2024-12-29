package run.scatter.botjde.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class Time {
  public static boolean isNowNearTime(LocalDateTime timeToCheckAgainst, int buffer, TimeUnit unit) {
    Duration duration = Duration.between(timeToCheckAgainst, LocalDateTime.now());

    if(unit == TimeUnit.HOURS) {
      return (Math.abs(duration.toHours())>buffer);
    }
    else if(unit == TimeUnit.MINUTES) {
      return (Math.abs(duration.toMinutes())>buffer);
    }
    else /*(unit == TimeUnit.SECONDS)*/{
      return (Math.abs(duration.toSeconds())>buffer);
    }
  }
}