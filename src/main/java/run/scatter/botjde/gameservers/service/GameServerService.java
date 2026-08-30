package run.scatter.botjde.gameservers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;
import run.scatter.botjde.gameservers.provider.GameServerProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service that orchestrates game server operations across registered providers.
 */
@Slf4j
@Service
public class GameServerService {

  private final AppConfig appConfig;
  private final Map<String, GameServerProvider> providers;

  public GameServerService(AppConfig appConfig, List<GameServerProvider> providerList) {
    this.appConfig = appConfig;
    this.providers = providerList.stream()
        .collect(Collectors.toMap(GameServerProvider::getType, p -> p));
    log.info("Initialized GameServerService with providers: {}", providers.keySet());
  }

  public List<GameServerConfig> getEnabledServers() {
    return appConfig.getGameservers().stream()
        .filter(GameServerConfig::isEnabled)
        .toList();
  }

  public Optional<GameServerConfig> findServer(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      return Optional.empty();
    }
    final String trimmed = identifier.trim();
    return appConfig.getGameservers().stream()
        .filter(GameServerConfig::isEnabled)
        .filter(s -> s.getId().equalsIgnoreCase(trimmed) || s.getName().equalsIgnoreCase(trimmed))
        .findFirst();
  }

  public Mono<List<GameServerStatus>> getAllStatuses() {
    final List<GameServerConfig> servers = getEnabledServers();
    if (servers.isEmpty()) {
      return Mono.just(List.of());
    }

    return Flux.fromIterable(servers)
        .flatMap(this::fetchStatusForConfig)
        .collectList();
  }

  public Mono<GameServerStatus> getStatus(String identifier) {
    return findServer(identifier)
        .map(this::fetchStatusForConfig)
        .orElseGet(() -> Mono.just(GameServerStatus.builder()
            .serverId(identifier)
            .serverName(identifier)
            .state(GameServerStatus.State.NOT_FOUND)
            .statusDetails("Server not configured in BotJDE")
            .build()));
  }

  public Mono<Void> start(String identifier) {
    final Optional<GameServerConfig> configOpt = findServer(identifier);
    if (configOpt.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Game server not found: " + identifier));
    }
    final GameServerConfig config = configOpt.get();
    final GameServerProvider provider = getProvider(config.getType());
    return provider.start(config);
  }

  public Mono<Void> stop(String identifier) {
    final Optional<GameServerConfig> configOpt = findServer(identifier);
    if (configOpt.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Game server not found: " + identifier));
    }
    final GameServerConfig config = configOpt.get();
    final GameServerProvider provider = getProvider(config.getType());
    return provider.stop(config);
  }

  public Mono<Void> restart(String identifier) {
    final Optional<GameServerConfig> configOpt = findServer(identifier);
    if (configOpt.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Game server not found: " + identifier));
    }
    final GameServerConfig config = configOpt.get();
    final GameServerProvider provider = getProvider(config.getType());
    return provider.restart(config);
  }

  public Mono<String> getLogs(String identifier, int tailLines) {
    final Optional<GameServerConfig> configOpt = findServer(identifier);
    if (configOpt.isEmpty()) {
      return Mono.just("Error: Game server not found (" + identifier + ")");
    }
    final GameServerConfig config = configOpt.get();
    final GameServerProvider provider = getProvider(config.getType());
    return provider.getLogs(config, tailLines);
  }

  private Mono<GameServerStatus> fetchStatusForConfig(GameServerConfig config) {
    final GameServerProvider provider = providers.get(config.getType());
    if (provider == null) {
      log.warn("No provider found for type: {}", config.getType());
      return Mono.just(GameServerStatus.builder()
          .serverId(config.getId())
          .serverName(config.getName())
          .state(GameServerStatus.State.ERROR)
          .statusDetails("Unsupported provider type: " + config.getType())
          .build());
    }
    return provider.getStatus(config);
  }

  private GameServerProvider getProvider(String type) {
    final GameServerProvider provider = providers.get(type);
    if (provider == null) {
      throw new IllegalStateException("Unsupported provider type: " + type);
    }
    return provider;
  }
}
