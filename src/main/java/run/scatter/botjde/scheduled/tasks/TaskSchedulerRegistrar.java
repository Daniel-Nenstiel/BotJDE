package run.scatter.botjde.scheduled.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;

/**
 * Auto-discovers all {@link ScheduledTask} Spring beans and registers their cron schedules
 * dynamically with Spring's task scheduler.
 */
@Slf4j
@Configuration
public class TaskSchedulerRegistrar implements SchedulingConfigurer {

  private final List<ScheduledTask> scheduledTasks;

  public TaskSchedulerRegistrar(List<ScheduledTask> scheduledTasks) {
    this.scheduledTasks = scheduledTasks;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    if (scheduledTasks.isEmpty()) {
      log.info("No ScheduledTask beans found to register.");
      return;
    }

    log.info("Registering {} scheduled task(s)...", scheduledTasks.size());

    for (ScheduledTask task : scheduledTasks) {
      if (task.isEnabled()) {
        log.info("Registering scheduled task '{}' with cron [{}]", task.getName(), task.getCronExpression());
        taskRegistrar.addCronTask(() -> runSafely(task), task.getCronExpression());
      } else {
        log.info("Scheduled task '{}' is disabled, skipping registration.", task.getName());
      }
    }
  }

  protected void runSafely(ScheduledTask task) {
    log.info("Executing scheduled task '{}'...", task.getName());
    final long startTime = System.currentTimeMillis();
    try {
      task.execute();
      final long elapsed = System.currentTimeMillis() - startTime;
      log.info("Completed scheduled task '{}' in {} ms", task.getName(), elapsed);
    } catch (Exception e) {
      log.error("Unhandled exception in scheduled task '{}': {}", task.getName(), e.getMessage(), e);
    }
  }
}
