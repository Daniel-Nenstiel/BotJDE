package run.scatter.botjde.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import run.scatter.botjde.entity.pocket.PocketSheetConfig;
import run.scatter.botjde.entity.Server;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppConfig {
  @Value("${app.config.source:yaml}") // Default to "yaml"
  private String configSource;

  private List<Server> servers = new ArrayList<>();
  private List<PocketSheetConfig> pocketSheets = new ArrayList<>();

  public void initializeConfig() {
    log.info("Initializing configuration with source: {}", configSource);

    switch (configSource.toLowerCase()) {
      case "yaml" -> loadFromYaml();
      case "database" -> loadFromDatabase();
      default -> {
        log.error("Unsupported configuration source: {}", configSource);
        throw new IllegalArgumentException("Unsupported configuration source: " + configSource);
      }
    }
  }

  private void loadFromYaml() {
    log.info("Loading configuration from YAML...");
    if (servers != null && !servers.isEmpty()) {
      servers.forEach(server -> log.info(
          "Loaded server: ID={}, defaultChannelId={}, puzzleChannelId={}, name={}, birthdaysEnabled={}, anniversariesEnabled={}, puzzlesEnabled={}",
          server.getId(),
          server.getDefaultChannelId(),
          server.getPuzzleChannelId(),
          server.getName(),
          server.isBirthdaysEnabled(),
          server.isAnniversariesEnabled(),
          server.isPuzzlesEnabled()
      ));
    } else {
      log.error("No servers defined in application.yml");
      throw new IllegalStateException("No servers defined in application.yml");
    }

    if (pocketSheets != null && !pocketSheets.isEmpty()) {
      pocketSheets.forEach(sheet -> log.info(
          "Loaded pocket sheet: ServerID={}, URLs={}",
          sheet.getServerId(),
          sheet.getUrls()
      ));
    } else {
      log.error("No pocket sheets defined in application.yml");
    }
  }

  private void loadFromDatabase() {
    log.info("Attempting to load configuration from the database...");
    if (servers == null) {
      servers = new ArrayList<>();
    }
    if (pocketSheets == null) {
      pocketSheets = new ArrayList<>();
    }
    // TODO: Add logic to load from database
  }
}
