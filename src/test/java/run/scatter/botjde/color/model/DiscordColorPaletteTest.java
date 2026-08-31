package run.scatter.botjde.color.model;

import org.junit.jupiter.api.Test;
import run.scatter.botjde.color.model.DiscordColorPalette.ResolvedColor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DiscordColorPaletteTest {

  @Test
  void swatches_contains16DefaultColors() {
    assertThat(DiscordColorPalette.SWATCHES).hasSize(16);
  }

  @Test
  void resolve_matchesPresetById() {
    Optional<ResolvedColor> result = DiscordColorPalette.resolve("teal");
    assertThat(result).isPresent();
    assertThat(result.get().hex()).isEqualTo("#1ABC9C");
    assertThat(result.get().name()).isEqualTo("Teal");
  }

  @Test
  void resolve_matchesPresetByDisplayNameCaseInsensitive() {
    Optional<ResolvedColor> result = DiscordColorPalette.resolve("DARK TEAL");
    assertThat(result).isPresent();
    assertThat(result.get().hex()).isEqualTo("#11806A");
    assertThat(result.get().name()).isEqualTo("Dark Teal");
  }

  @Test
  void resolve_matchesCustomHexWithHash() {
    Optional<ResolvedColor> result = DiscordColorPalette.resolve("#FF5733");
    assertThat(result).isPresent();
    assertThat(result.get().hex()).isEqualTo("#FF5733");
    assertThat(result.get().color().getRGB()).isEqualTo(0xFF5733);
  }

  @Test
  void resolve_matchesCustomHexWithoutHash() {
    Optional<ResolvedColor> result = DiscordColorPalette.resolve("00FF00");
    assertThat(result).isPresent();
    assertThat(result.get().hex()).isEqualTo("#00FF00");
    assertThat(result.get().color().getRGB()).isEqualTo(0x00FF00);
  }

  @Test
  void resolve_returnsEmptyForInvalidInputs() {
    assertThat(DiscordColorPalette.resolve(null)).isEmpty();
    assertThat(DiscordColorPalette.resolve("")).isEmpty();
    assertThat(DiscordColorPalette.resolve("not-a-color")).isEmpty();
    assertThat(DiscordColorPalette.resolve("#12345")).isEmpty(); // 5 digits
    assertThat(DiscordColorPalette.resolve("#1234567")).isEmpty(); // 7 digits
    assertThat(DiscordColorPalette.resolve("#GGGGGG")).isEmpty(); // non-hex
  }

  @Test
  void random_generatesValidColor() {
    ResolvedColor randomColor = DiscordColorPalette.random();
    assertThat(randomColor).isNotNull();
    assertThat(randomColor.hex()).matches("^#[0-9A-F]{6}$");
    assertThat(randomColor.color()).isNotNull();
  }
}
