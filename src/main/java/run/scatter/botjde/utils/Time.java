package run.scatter.botjde.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Time {
  public static boolean isNowNearTime(LocalDateTime timeToCheckAgainst, int threshold, ChronoUnit unit) {
    if (unit == null) {
      throw new IllegalArgumentException("Time unit cannot be null");
    }

    long diff = switch (unit) {
      case HOURS -> Math.abs(ChronoUnit.HOURS.between(timeToCheckAgainst, LocalDateTime.now()));
      case MINUTES -> Math.abs(ChronoUnit.MINUTES.between(timeToCheckAgainst, LocalDateTime.now()));
      case SECONDS -> Math.abs(ChronoUnit.SECONDS.between(timeToCheckAgainst, LocalDateTime.now()));
      default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
    };

    return diff <= threshold;
  }
}
