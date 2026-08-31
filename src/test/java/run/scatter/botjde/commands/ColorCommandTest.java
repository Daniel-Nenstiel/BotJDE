package run.scatter.botjde.commands;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.color.model.DiscordColorPalette;
import run.scatter.botjde.color.service.ColorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ColorCommandTest {

  private ColorService mockColorService;
  private ColorCommand colorCommand;

  @BeforeEach
  void setUp() {
    mockColorService = mock(ColorService.class);
    when(mockColorService.getPalette()).thenReturn(DiscordColorPalette.SWATCHES);
    colorCommand = new ColorCommand(mockColorService);
  }

  @Test
  void getName_returnsColor() {
    assertThat(colorCommand.getName()).isEqualTo("color");
  }

  @Test
  void getCommandDefinition_containsSubcommands() {
    ApplicationCommandRequest def = colorCommand.getCommandDefinition();
    assertThat(def.name()).isEqualTo("color");
    assertThat(def.options().get()).isNotEmpty();

    List<ApplicationCommandOptionData> options = def.options().get();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("set"))).isTrue();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("remove"))).isTrue();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("random"))).isTrue();
    assertThat(options.stream().anyMatch(opt -> opt.name().equals("list"))).isTrue();
  }
}
