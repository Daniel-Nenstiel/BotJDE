package run.scatter.botjde.entity.pocket;

import discord4j.common.util.Snowflake;
import lombok.Data;

import java.util.List;

@Data
public class PocketSheetConfig {

  private Snowflake serverId;
  private List<SheetUrl> urls;

  @Data
  public static class SheetUrl {
    private String name;
    private String url;
  }
}
