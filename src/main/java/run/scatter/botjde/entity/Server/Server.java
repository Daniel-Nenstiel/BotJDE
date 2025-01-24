package run.scatter.botjde.entity.Server;

import discord4j.common.util.Snowflake;
import lombok.Data;

@Data
public class Server {
  private String name;
  private Snowflake serverId;
  private String defaultChannelId;
  private String puzzleChannelId;

  public Server(String name, Long serverId) {
    this.name = name;
    this.serverId = Snowflake.of(serverId);
  }

  public void setServerId(Long serverId) { this.serverId = Snowflake.of(serverId); }
}
