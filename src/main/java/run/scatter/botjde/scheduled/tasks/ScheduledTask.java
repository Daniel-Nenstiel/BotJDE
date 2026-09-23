package run.scatter.botjde.scheduled.tasks;

/**
 * Defines a scheduled background maintenance or operational task.
 * <p>
 * To add a new scheduled task, create a Spring {@code @Component} implementing this interface.
 * It will be auto-discovered and registered by {@link TaskSchedulerRegistrar}.
 */
public interface ScheduledTask {

  /**
   * Unique name of the task for identification and logging.
   *
   * @return task name
   */
  String getName();

  /**
   * Standard 6-field Spring cron expression (second, minute, hour, day-of-month, month, day-of-week).
   * Example: "0 0 4 * * ?" for daily at 4:00 AM.
   *
   * @return cron expression
   */
  String getCronExpression();

  /**
   * Executes the task logic.
   */
  void execute();

  /**
   * Whether the task is enabled and should be scheduled.
   *
   * @return true if enabled, false otherwise
   */
  default boolean isEnabled() {
    return true;
  }
}
