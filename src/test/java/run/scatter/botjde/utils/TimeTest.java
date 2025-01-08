package run.scatter.botjde.utils;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimeTest {

  @Test
  void testIsNowNearTime_withinThresholdInHours() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneHourAgo = now.minusHours(1);

    // Act & Assert
    assertTrue(Time.isNowNearTime(oneHourAgo, 1, ChronoUnit.HOURS));  // Within 1 hour
    assertFalse(Time.isNowNearTime(oneHourAgo, 0, ChronoUnit.HOURS)); // More than 0 hours
  }

  @Test
  void testIsNowNearTime_withinThresholdInMinutes() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime fiveMinutesAgo = now.minusMinutes(5);

    // Act & Assert
    assertTrue(Time.isNowNearTime(fiveMinutesAgo, 5, ChronoUnit.MINUTES));  // Within 5 minutes
    assertFalse(Time.isNowNearTime(fiveMinutesAgo, 4, ChronoUnit.MINUTES)); // More than 4 minutes
  }

  @Test
  void testIsNowNearTime_withinThresholdInSeconds() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsAgo = now.minusSeconds(10);

    // Act & Assert
    assertTrue(Time.isNowNearTime(tenSecondsAgo, 10, ChronoUnit.SECONDS));  // Within 10 seconds
    assertFalse(Time.isNowNearTime(tenSecondsAgo, 9, ChronoUnit.SECONDS));  // More than 9 seconds
  }

  @Test
  void testIsNowNearTime_exactBoundary() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime boundaryTime = now.minusSeconds(30);

    // Act & Assert
    assertTrue(Time.isNowNearTime(boundaryTime, 30, ChronoUnit.SECONDS)); // Exactly at boundary
  }

  @Test
  void testIsNowNearTime_edgeCaseSameTime() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();

    // Act & Assert
    assertTrue(Time.isNowNearTime(now, 0, ChronoUnit.SECONDS));  // Same time, should return true for 0 threshold
  }

  @Test
  void testIsNowNearTime_throwsExceptionForUnsupportedUnits() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Time.isNowNearTime(LocalDateTime.now(), 5, null));
  }
}
