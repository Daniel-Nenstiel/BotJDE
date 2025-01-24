package run.scatter.botjde.config;

import discord4j.common.util.Snowflake;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppConfig implements InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

  @Getter
  @Setter
  private List<Server> servers;

  @Value("${app.config.source:}")
  private String configSource;

  public void setConfigSource(String configSource) {
    this.configSource = configSource;
  }

  @Override
  public void afterPropertiesSet() {
    logger.info("Initializing configuration with source: {}", configSource);

    switch (configSource.toLowerCase()) {
      case "yaml" -> loadFromYaml();
      case "database" -> loadFromDatabase();
      default -> {
        logger.error("Unsupported configuration source: {}", configSource);
        throw new RuntimeException("Unsupported configuration source: " + configSource);
      }
    }
  }

  private void loadFromYaml() {
    if (servers != null && !servers.isEmpty()) {
      servers.forEach(server -> logger.info(
          "Loaded server: ID={}, defaultChannelId={}, puzzleChannelId={}, name={}, birthdaysEnabled={}, anniversariesEnabled={}, puzzlesEnabled={}",
          server.getId(),
          server.getDefaultChannelId(),
          server.getPuzzleChannelId(),
          server.getName(),
          server.isBirthdaysEnabled(),
          server.isAnniversariesEnabled(),
          server.isPuzzlesEnabled()
      ));
      logger.info("Configuration successfully loaded from application.yml");
    } else {
      logger.error("No servers defined in application.yml");
      throw new RuntimeException("No servers defined in application.yml");
    }
  }

  private void loadFromDatabase() {
    if (servers == null) {
      servers = new ArrayList<>();
    }
    // Add logic to load servers from the database
    logger.info("Configuration successfully loaded from database with {} servers", servers.size());
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Server {
    private String name;
    private Snowflake id;
    private Snowflake defaultChannelId;
    private Snowflake puzzleChannelId;
    private boolean birthdaysEnabled;
    private boolean anniversariesEnabled;
    private boolean puzzlesEnabled;
  }

  @Component
  @ConfigurationPropertiesBinding
  public static class SnowflakeConverter implements Converter<Long, Snowflake> {
    @Override
    public Snowflake convert(@Nullable Long source) {
      return source == null ? null : Snowflake.of(source);
    }
  }
}
