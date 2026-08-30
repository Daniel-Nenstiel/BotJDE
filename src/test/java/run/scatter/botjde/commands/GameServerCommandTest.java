package run.scatter.botjde.commands;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.gameservers.model.GameServerConfig;
import run.scatter.botjde.gameservers.service.GameServerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServerCommandTest {

  private GameServerService mockService;
  private GameServerCommand command;

  @BeforeEach
  void setUp() {
    mockService = mock(GameServerService.class);
    when(mockService.getEnabledServers()).thenReturn(List.of(
        GameServerConfig.builder()
            .id("zomboid")
            .name("Project Zomboid")
            .type("docker")
            .target("project-zomboid")
            .enabled(true)
            .build()
    ));

    command = new GameServerCommand(mockService);
  }

  @Test
  void getName_returnsServer() {
    assertThat(command.getName()).isEqualTo("server");
  }

  @Test
  void getCommandDefinition_containsSubcommands() {
    ApplicationCommandRequest def = command.getCommandDefinition();
    assertThat(def.name()).isEqualTo("server");
    assertThat(def.options().get()).isNotEmpty();

    List<ApplicationCommandOptionData> options = def.options().get();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("status"))).isTrue();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("zomboid"))).isTrue();
  }
}
