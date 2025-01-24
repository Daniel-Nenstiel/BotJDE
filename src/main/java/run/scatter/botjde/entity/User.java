package run.scatter.botjde.entity;

import discord4j.common.util.Snowflake;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import run.scatter.botjde.entity.Server.Server;

@Data
@RequiredArgsConstructor
public class User {
  private String name;
  private String username;
  private Snowflake userId;
  private Snowflake serverId;

  public void setDiscordId(Long discordId) {
    this.userId = Snowflake.of(discordId);
  }
  public void setServerId(Long serverId) {
    this.userId = Snowflake.of(serverId);
  }
}
