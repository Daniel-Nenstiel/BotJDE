package run.scatter.botjde.entity;

import discord4j.common.util.Snowflake;
import lombok.Data;

@Data
public class Server {
  private Snowflake id;
  private String name;
  private Snowflake defaultChannelId;
  private Snowflake puzzleChannelId;
  private boolean birthdaysEnabled;
  private boolean anniversariesEnabled;
  private boolean puzzlesEnabled;
}