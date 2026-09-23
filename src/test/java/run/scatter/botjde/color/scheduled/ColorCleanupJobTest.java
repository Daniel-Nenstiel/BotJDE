package run.scatter.botjde.color.scheduled;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.service.ColorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ColorCleanupJobTest {

  private ColorService mockColorService;
  private GatewayDiscordClient mockClient;
  private ColorCleanupJob cleanupJob;

  @BeforeEach
  void setUp() {
    mockColorService = mock(ColorService.class);
    mockClient = mock(GatewayDiscordClient.class);
    cleanupJob = new ColorCleanupJob(mockColorService, mockClient);
  }

  @Test
  void taskMetadata_returnsExpectedValues() {
    assertThat(cleanupJob.getName()).isEqualTo("colorCleanup");
    assertThat(cleanupJob.getCronExpression()).isEqualTo("0 0 4 * * ?");
    assertThat(cleanupJob.isEnabled()).isTrue();
  }

  @Test
  void execute_whenClientNull_doesNotThrow() {
    ColorCleanupJob jobWithoutClient = new ColorCleanupJob(mockColorService, null);
    jobWithoutClient.execute();
    verifyNoInteractions(mockColorService);
  }

  @Test
  void execute_executesForConnectedGuilds() {
    Guild mockGuild = mock(Guild.class);
    when(mockGuild.getName()).thenReturn("Test Guild");
    when(mockClient.getGuilds()).thenReturn(Flux.just(mockGuild));
    when(mockColorService.cleanupOrphanColorRoles(mockGuild)).thenReturn(Mono.just(2L));

    cleanupJob.execute();

    verify(mockColorService, times(1)).cleanupOrphanColorRoles(mockGuild);
  }
}
