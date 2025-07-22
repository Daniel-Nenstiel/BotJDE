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
}