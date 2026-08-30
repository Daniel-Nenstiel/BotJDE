package run.scatter.botjde.gameservers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration model for a registered game server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameServerConfig {

  /** Unique identifier / slug for commands (e.g. "zomboid"). */
  private String id;

  /** Human-readable display name (e.g. "Project Zomboid"). */
  private String name;

  /** Provider type (e.g. "docker"). */
  @Builder.Default
  private String type = "docker";

  /** Target identifier for provider (e.g. container name "project-zomboid"). */
  private String target;

  /** Whether this game server is currently enabled in the bot. */
  @Builder.Default
  private boolean enabled = true;

  /** Optional short description. */
  private String description;
}
