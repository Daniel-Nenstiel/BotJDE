package run.scatter.botjde.gameservers.provider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * GameServerProvider implementation that manages game servers running in Docker containers.
 */
@Slf4j
@Component
public class DockerGameServerProvider implements GameServerProvider {

  private static final int DEFAULT_LOG_LINES = 25;
  private static final int LOG_AWAIT_TIMEOUT_SECONDS = 15;

  private final DockerClient dockerClient;

  public DockerGameServerProvider(@Autowired(required = false) DockerClient dockerClient) {
    this.dockerClient = dockerClient;
  }

  @Override
  public String getType() {
    return "docker";
  }

  @Override
  public Mono<GameServerStatus> getStatus(GameServerConfig config) {
    return Mono.fromCallable(() -> {
      if (dockerClient == null) {
        return GameServerStatus.builder()
            .serverId(config.getId())
            .serverName(config.getName())
            .state(GameServerStatus.State.ERROR)
            .statusDetails("Docker client is unavailable")
            .build();
      }

      try {
        final InspectContainerResponse response = dockerClient.inspectContainerCmd(config.getTarget()).exec();
        final InspectContainerResponse.ContainerState state = response.getState();

        GameServerStatus.State resolvedState = GameServerStatus.State.UNKNOWN;
        if (Boolean.TRUE.equals(state.getRunning())) {
          resolvedState = GameServerStatus.State.RUNNING;
        } else if (Boolean.TRUE.equals(state.getRestarting())) {
          resolvedState = GameServerStatus.State.RESTARTING;
        } else if (Boolean.FALSE.equals(state.getRunning())) {
          resolvedState = GameServerStatus.State.STOPPED;
        }

        String healthStatus = null;
        if (state.getHealth() != null) {
          healthStatus = state.getHealth().getStatus();
        }

        return GameServerStatus.builder()
            .serverId(config.getId())
            .serverName(config.getName())
            .state(resolvedState)
            .statusDetails(state.getStatus())
            .health(healthStatus)
            .build();

      } catch (NotFoundException e) {
        log.warn("Container not found for game server {}: {}", config.getId(), config.getTarget());
        return GameServerStatus.builder()
            .serverId(config.getId())
            .serverName(config.getName())
            .state(GameServerStatus.State.NOT_FOUND)
            .statusDetails("Container not found (" + config.getTarget() + ")")
            .build();
      } catch (Exception e) {
        log.error("Error inspecting container for {}: {}", config.getId(), e.getMessage(), e);
        return GameServerStatus.builder()
            .serverId(config.getId())
            .serverName(config.getName())
            .state(GameServerStatus.State.ERROR)
            .statusDetails("Error: " + e.getMessage())
            .build();
      }
    }).subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<Void> start(GameServerConfig config) {
    return Mono.<Void>fromRunnable(() -> {
      if (dockerClient == null) {
        throw new IllegalStateException("Docker client is unavailable");
      }
      log.info("Starting Docker container for game server '{}' (container: '{}')", config.getName(), config.getTarget());
      dockerClient.startContainerCmd(config.getTarget()).exec();
    }).subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<Void> stop(GameServerConfig config) {
    return Mono.<Void>fromRunnable(() -> {
      if (dockerClient == null) {
        throw new IllegalStateException("Docker client is unavailable");
      }
      log.info("Stopping Docker container for game server '{}' (container: '{}')", config.getName(), config.getTarget());
      dockerClient.stopContainerCmd(config.getTarget()).exec();
    }).subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<Void> restart(GameServerConfig config) {
    return Mono.<Void>fromRunnable(() -> {
      if (dockerClient == null) {
        throw new IllegalStateException("Docker client is unavailable");
      }
      log.info("Restarting Docker container for game server '{}' (container: '{}')", config.getName(), config.getTarget());
      dockerClient.restartContainerCmd(config.getTarget()).exec();
    }).subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<String> getLogs(GameServerConfig config, int tailLines) {
    return Mono.fromCallable(() -> {
      if (dockerClient == null) {
        return "Error: Docker client is unavailable";
      }

      final StringBuilder logOutput = new StringBuilder();
      final int lines = tailLines > 0 ? tailLines : DEFAULT_LOG_LINES;

      try {
        final ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>() {
          @Override
          public void onNext(Frame item) {
            if (item != null && item.getPayload() != null) {
              logOutput.append(new String(item.getPayload(), StandardCharsets.UTF_8));
            }
          }
        };

        dockerClient.logContainerCmd(config.getTarget())
            .withStdOut(true)
            .withStdErr(true)
            .withTail(lines)
            .withTimestamps(false)
            .exec(callback)
            .awaitCompletion(LOG_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        final String result = logOutput.toString();
        return result.isBlank() ? "No recent log output." : result;

      } catch (NotFoundException e) {
        return "Error: Container not found (" + config.getTarget() + ")";
      } catch (Exception e) {
        log.error("Error fetching logs for {}: {}", config.getId(), e.getMessage(), e);
        return "Error fetching logs: " + e.getMessage();
      }
    }).subscribeOn(Schedulers.boundedElastic());
  }
}
