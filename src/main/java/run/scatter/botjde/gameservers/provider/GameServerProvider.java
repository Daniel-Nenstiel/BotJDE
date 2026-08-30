package run.scatter.botjde.gameservers.provider;

import reactor.core.publisher.Mono;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.model.GameServerStatus;

/**
 * Strategy interface for game server management providers (e.g. Docker, Systemd, etc.).
 */
public interface GameServerProvider {

  /**
   * Identifies the provider type (e.g. "docker").
   *
   * @return provider type string
   */
  String getType();

  /**
   * Retrieves the current status of the game server.
   *
   * @param config the game server configuration
   * @return a Mono emitting the current status
   */
  Mono<GameServerStatus> getStatus(GameServerConfig config);

  /**
   * Starts the game server.
   *
   * @param config the game server configuration
   * @return a Mono representing completion
   */
  Mono<Void> start(GameServerConfig config);

  /**
   * Gracefully stops the game server.
   *
   * @param config the game server configuration
   * @return a Mono representing completion
   */
  Mono<Void> stop(GameServerConfig config);

  /**
   * Restarts the game server.
   *
   * @param config the game server configuration
   * @return a Mono representing completion
   */
  Mono<Void> restart(GameServerConfig config);

  /**
   * Fetches the recent log output from the game server.
   *
   * @param config the game server configuration
   * @param tailLines number of recent lines to retrieve
   * @return a Mono emitting the formatted log string
   */
  Mono<String> getLogs(GameServerConfig config, int tailLines);
}
