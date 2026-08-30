package run.scatter.botjde.gameservers.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

/**
 * Spring configuration creating the DockerClient bean.
 */
@Slf4j
@Configuration
public class DockerClientConfiguration {

  private static final int MAX_CONNECTIONS = 50;
  private static final int CONNECTION_TIMEOUT_SECONDS = 30;
  private static final int RESPONSE_TIMEOUT_SECONDS = 45;

  /**
   * Creates and configures the default DockerClient bean connected to the local Docker socket.
   *
   * @return DockerClient instance
   */
  @Bean
  public DockerClient dockerClient() {
    try {
      final String dockerHost = System.getenv().getOrDefault("DOCKER_HOST", "unix:///var/run/docker.sock");
      final DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
          .withDockerHost(dockerHost)
          .build();
      final DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
          .dockerHost(URI.create(dockerHost))
          .sslConfig(config.getSSLConfig())
          .maxConnections(MAX_CONNECTIONS)
          .connectionTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS))
          .responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS))
          .build();
      return DockerClientImpl.getInstance(config, httpClient);
    } catch (Exception e) {
      log.warn("Unable to initialize DockerClient: {}", e.getMessage());
      return null;
    }
  }
}
