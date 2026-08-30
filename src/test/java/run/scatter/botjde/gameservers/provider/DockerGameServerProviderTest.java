package run.scatter.botjde.gameservers.provider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.RestartContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DockerGameServerProviderTest {

  private DockerClient mockDockerClient;
  private DockerGameServerProvider provider;
  private GameServerConfig config;

  @BeforeEach
  void setUp() {
    mockDockerClient = mock(DockerClient.class);
    provider = new DockerGameServerProvider(mockDockerClient);
    config = GameServerConfig.builder()
        .id("zomboid")
        .name("Project Zomboid")
        .type("docker")
        .target("project-zomboid")
        .enabled(true)
        .build();
  }

  @Test
  void getType_returnsDocker() {
    assertThat(provider.getType()).isEqualTo("docker");
  }

  @Test
  void getStatus_returnsRunning_whenContainerIsRunning() {
    InspectContainerCmd inspectCmd = mock(InspectContainerCmd.class);
    InspectContainerResponse response = mock(InspectContainerResponse.class);
    InspectContainerResponse.ContainerState state = mock(InspectContainerResponse.ContainerState.class);

    when(mockDockerClient.inspectContainerCmd("project-zomboid")).thenReturn(inspectCmd);
    when(inspectCmd.exec()).thenReturn(response);
    when(response.getState()).thenReturn(state);
    when(state.getRunning()).thenReturn(true);
    when(state.getStatus()).thenReturn("running");

    GameServerStatus status = provider.getStatus(config).block();

    assertThat(status).isNotNull();
    assertThat(status.getState()).isEqualTo(GameServerStatus.State.RUNNING);
    assertThat(status.getStatusDetails()).isEqualTo("running");
  }

  @Test
  void getStatus_returnsNotFound_whenContainerDoesNotExist() {
    InspectContainerCmd inspectCmd = mock(InspectContainerCmd.class);
    when(mockDockerClient.inspectContainerCmd("project-zomboid")).thenReturn(inspectCmd);
    when(inspectCmd.exec()).thenThrow(new NotFoundException("Container not found"));

    GameServerStatus status = provider.getStatus(config).block();

    assertThat(status).isNotNull();
    assertThat(status.getState()).isEqualTo(GameServerStatus.State.NOT_FOUND);
  }

  @Test
  void start_executesStartContainerCmd() {
    StartContainerCmd startCmd = mock(StartContainerCmd.class);
    when(mockDockerClient.startContainerCmd("project-zomboid")).thenReturn(startCmd);

    provider.start(config).block();

    verify(startCmd, times(1)).exec();
  }

  @Test
  void stop_executesStopContainerCmd() {
    StopContainerCmd stopCmd = mock(StopContainerCmd.class);
    when(mockDockerClient.stopContainerCmd("project-zomboid")).thenReturn(stopCmd);

    provider.stop(config).block();

    verify(stopCmd, times(1)).exec();
  }

  @Test
  void restart_executesRestartContainerCmd() {
    RestartContainerCmd restartCmd = mock(RestartContainerCmd.class);
    when(mockDockerClient.restartContainerCmd("project-zomboid")).thenReturn(restartCmd);

    provider.restart(config).block();

    verify(restartCmd, times(1)).exec();
  }
}
