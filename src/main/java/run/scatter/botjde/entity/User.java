package run.scatter.botjde.entity;

import discord4j.common.util.Snowflake;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {
  private String name;
  private String username;
  private Snowflake userId;
  private Snowflake serverId;

  // MyBatis Constructor
  public User(String name, String username, Long userId, Long serverId) {
    this.name = name;
    this.username = username;
    this.userId = Snowflake.of(userId);
    this.serverId = Snowflake.of(serverId);
  }

  public void setDiscordId(Long discordId) {
    this.userId = Snowflake.of(discordId);
  }

  public void setServerId(Long serverId) {
    this.serverId = Snowflake.of(serverId);
  }
}
