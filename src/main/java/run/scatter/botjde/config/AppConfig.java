package run.scatter.botjde.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
      log.info("Configuration successfully loaded from application.yml");
    } else {
      log.error("No servers defined in application.yml");
      throw new IllegalStateException("No servers defined in application.yml");
    }
  }

  /*TODO*/
  private void loadFromDatabase() {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    log.info("Attempting to load configuration from the database...");
    // Stub for database loading logic
  }
}
