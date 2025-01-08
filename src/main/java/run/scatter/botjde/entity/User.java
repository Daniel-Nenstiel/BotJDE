package run.scatter.botjde.entity;

import discord4j.common.util.Snowflake;
import lombok.Data;

@Data
public class User {
  private String name;
  private String username;
  private Snowflake discordId;

  public User(String name, String username, Long discordId) {
    this.name = name;
    this.username = username;
    this.discordId = Snowflake.of(discordId);
  }

  public void setDiscordId(Long discordId) {
    this.discordId = Snowflake.of(discordId);
  }
}
