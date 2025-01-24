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
  private Snowflake discordId;
  private Server server;

  public void setDiscordId(Long discordId) {
    this.discordId = Snowflake.of(discordId);
  }
}
