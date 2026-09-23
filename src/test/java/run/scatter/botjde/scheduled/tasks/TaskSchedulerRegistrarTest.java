package run.scatter.botjde.scheduled.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TaskSchedulerRegistrarTest {

  @Test
  void configureTasks_withEmptyTaskList_doesNotRegisterTasks() {
    ScheduledTaskRegistrar registrar = mock(ScheduledTaskRegistrar.class);
    TaskSchedulerRegistrar configurer = new TaskSchedulerRegistrar(List.of());

    configurer.configureTasks(registrar);

    verify(registrar, never()).addCronTask(any(Runnable.class), anyString());
  }

  @Test
  void configureTasks_registersEnabledTasks() {
    ScheduledTask enabledTask = mock(ScheduledTask.class);
    when(enabledTask.getName()).thenReturn("testTask");
    when(enabledTask.getCronExpression()).thenReturn("0 0 4 * * ?");
    when(enabledTask.isEnabled()).thenReturn(true);

    ScheduledTask disabledTask = mock(ScheduledTask.class);
    when(disabledTask.getName()).thenReturn("disabledTask");
    when(disabledTask.isEnabled()).thenReturn(false);

    ScheduledTaskRegistrar registrar = mock(ScheduledTaskRegistrar.class);
    TaskSchedulerRegistrar configurer = new TaskSchedulerRegistrar(List.of(enabledTask, disabledTask));

    configurer.configureTasks(registrar);

    verify(registrar, times(1)).addCronTask(any(Runnable.class), eq("0 0 4 * * ?"));
  }

  @Test
  void runSafely_catchesExceptionsGracefully() {
    ScheduledTask failingTask = mock(ScheduledTask.class);
    when(failingTask.getName()).thenReturn("failingTask");
    doThrow(new RuntimeException("Simulated task error")).when(failingTask).execute();

    TaskSchedulerRegistrar configurer = new TaskSchedulerRegistrar(List.of(failingTask));

    // Must not throw
    configurer.runSafely(failingTask);

    verify(failingTask, times(1)).execute();
  }
}
