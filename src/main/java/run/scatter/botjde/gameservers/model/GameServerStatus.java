package run.scatter.botjde.gameservers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Live status report for a game server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameServerStatus {

  /**
   * Possible states of a game server.
   */
  public enum State {
    RUNNING,
    STOPPED,
    STARTING,
    STOPPING,
    RESTARTING,
    ERROR,
    NOT_FOUND,
    UNKNOWN
  }

  private String serverId;
  private String serverName;
  private State state;
  private String statusDetails;
  private String uptime;
  private String health;
}
