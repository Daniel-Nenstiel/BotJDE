package run.scatter.botjde.gameservers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;
import run.scatter.botjde.gameservers.provider.GameServerProvider;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServerServiceTest {

  private AppConfig appConfig;
  private GameServerProvider mockProvider;
  private GameServerService service;

  private GameServerConfig zomboidConfig;

  @BeforeEach
  void setUp() {
    appConfig = new AppConfig();
    zomboidConfig = GameServerConfig.builder()
        .id("zomboid")
        .name("Project Zomboid")
        .type("docker")
        .target("project-zomboid")
        .enabled(true)
        .build();
    appConfig.setGameservers(List.of(zomboidConfig));

    mockProvider = mock(GameServerProvider.class);
    when(mockProvider.getType()).thenReturn("docker");

    service = new GameServerService(appConfig, List.of(mockProvider));
  }

  @Test
  void findServer_matchesCaseInsensitive() {
    Optional<GameServerConfig> byId = service.findServer("ZOMBOID");
    assertThat(byId).isPresent();
    assertThat(byId.get().getId()).isEqualTo("zomboid");

    Optional<GameServerConfig> byName = service.findServer("project zomboid");
    assertThat(byName).isPresent();
    assertThat(byName.get().getId()).isEqualTo("zomboid");
  }

  @Test
  void findServer_returnsEmptyForUnknown() {
    Optional<GameServerConfig> unknown = service.findServer("minecraft");
    assertThat(unknown).isEmpty();
  }

  @Test
  void getAllStatuses_callsProviderForEachEnabledServer() {
    GameServerStatus mockStatus = GameServerStatus.builder()
        .serverId("zomboid")
        .serverName("Project Zomboid")
        .state(GameServerStatus.State.RUNNING)
        .build();
    when(mockProvider.getStatus(zomboidConfig)).thenReturn(Mono.just(mockStatus));

    List<GameServerStatus> statuses = service.getAllStatuses().block();

    assertThat(statuses).hasSize(1);
    assertThat(statuses.get(0).getState()).isEqualTo(GameServerStatus.State.RUNNING);
    verify(mockProvider, times(1)).getStatus(zomboidConfig);
  }

  @Test
  void start_delegatesToProvider() {
    when(mockProvider.start(zomboidConfig)).thenReturn(Mono.empty());

    service.start("zomboid").block();

    verify(mockProvider, times(1)).start(zomboidConfig);
  }

  @Test
  void stop_delegatesToProvider() {
    when(mockProvider.stop(zomboidConfig)).thenReturn(Mono.empty());

    service.stop("zomboid").block();

    verify(mockProvider, times(1)).stop(zomboidConfig);
  }

  @Test
  void restart_delegatesToProvider() {
    when(mockProvider.restart(zomboidConfig)).thenReturn(Mono.empty());

    service.restart("zomboid").block();

    verify(mockProvider, times(1)).restart(zomboidConfig);
  }

  @Test
  void getLogs_delegatesToProvider() {
    when(mockProvider.getLogs(zomboidConfig, 25)).thenReturn(Mono.just("Sample log lines"));

    String logs = service.getLogs("zomboid", 25).block();

    assertThat(logs).isEqualTo("Sample log lines");
    verify(mockProvider, times(1)).getLogs(zomboidConfig, 25);
  }
}
