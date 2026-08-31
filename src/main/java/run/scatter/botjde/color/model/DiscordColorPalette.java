package run.scatter.botjde.color.model;

import discord4j.rest.util.Color;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Palette containing Discord's 16 default role swatch colors and hex code parsing utilities.
 */
public final class DiscordColorPalette {

  private static final Pattern HEX_PATTERN = Pattern.compile("^#?([0-9a-fA-F]{6})$");
  private static final Random RANDOM = new Random();

  private static final int COLOR_RADIX = 16;
  private static final int MAX_RGB_VALUE = 256;

  /**
   * Represents a preset color swatch from Discord's default palette.
   *
   * @param id internal identifier (e.g. "teal")
   * @param displayName formatted human-readable name
   * @param hex hexadecimal color code (e.g. "#1ABC9C")
   * @param color Discord4J Color object
   * @param emoji visual emoji representation
   */
  public record Swatch(String id, String displayName, String hex, Color color, String emoji) {}

  /**
   * Represents a resolved color ready to be assigned as a role.
   *
   * @param name display name or hex representation
   * @param hex 6-digit hex code with # prefix
   * @param color Discord4J Color object
   */
  public record ResolvedColor(String name, String hex, Color color) {}

  public static final List<Swatch> SWATCHES = List.of(
      new Swatch("teal", "Teal", "#1ABC9C", Color.of(0x1A, 0xBC, 0x9C), "🟢"),
      new Swatch("dark-teal", "Dark Teal", "#11806A", Color.of(0x11, 0x80, 0x6A), "🌲"),
      new Swatch("green", "Green", "#2ECC71", Color.of(0x2E, 0xCC, 0x71), "🟩"),
      new Swatch("dark-green", "Dark Green", "#1F8B4C", Color.of(0x1F, 0x8B, 0x4C), "🌿"),
      new Swatch("blue", "Blue", "#3498DB", Color.of(0x34, 0x98, 0xDB), "🟦"),
      new Swatch("dark-blue", "Dark Blue", "#206694", Color.of(0x20, 0x66, 0x94), "🌊"),
      new Swatch("purple", "Purple", "#9B59B6", Color.of(0x9B, 0x59, 0xB6), "🟪"),
      new Swatch("dark-purple", "Dark Purple", "#71368A", Color.of(0x71, 0x36, 0x8A), "🍆"),
      new Swatch("magenta", "Magenta", "#E91E63", Color.of(0xE9, 0x1E, 0x63), "💖"),
      new Swatch("dark-magenta", "Dark Magenta", "#AD1457", Color.of(0xAD, 0x14, 0x57), "🍷"),
      new Swatch("gold", "Gold", "#F1C40F", Color.of(0xF1, 0xC4, 0x0F), "🟨"),
      new Swatch("dark-gold", "Dark Gold", "#C27C0E", Color.of(0xC2, 0x7C, 0x0E), "🍯"),
      new Swatch("orange", "Orange", "#E67E22", Color.of(0xE6, 0x7E, 0x22), "🟧"),
      new Swatch("dark-orange", "Dark Orange", "#A84300", Color.of(0xA8, 0x43, 0x00), "🍂"),
      new Swatch("red", "Red", "#E74C3C", Color.of(0xE7, 0x4C, 0x3C), "🟥"),
      new Swatch("dark-red", "Dark Red", "#992D22", Color.of(0x99, 0x2D, 0x22), "🛑")
  );

  private DiscordColorPalette() {
    // Utility class
  }

  /**
   * Resolves a color from either a preset swatch name or a custom hex code.
   *
   * @param input the swatch name (e.g. "teal", "dark-teal") or hex code (e.g. "#FF5733", "FF5733")
   * @return an Optional containing the ResolvedColor, or empty if input is invalid
   */
  public static Optional<ResolvedColor> resolve(String input) {
    if (input == null || input.isBlank()) {
      return Optional.empty();
    }

    final String normalized = input.trim().toLowerCase().replace(" ", "-");

    // 1. Check preset swatches
    for (Swatch swatch : SWATCHES) {
      if (swatch.id().equalsIgnoreCase(normalized) || swatch.displayName().equalsIgnoreCase(input.trim())) {
        return Optional.of(new ResolvedColor(swatch.displayName(), swatch.hex(), swatch.color()));
      }
    }

    // 2. Check custom hex code
    final Matcher matcher = HEX_PATTERN.matcher(input.trim());
    if (matcher.matches()) {
      final String hexDigits = matcher.group(1).toUpperCase();
      final int rgb = Integer.parseInt(hexDigits, COLOR_RADIX);
      final Color color = Color.of(rgb);
      final String hexCode = "#" + hexDigits;
      return Optional.of(new ResolvedColor(hexCode, hexCode, color));
    }

    return Optional.empty();
  }

  /**
   * Generates a random vibrant RGB color.
   *
   * @return a randomly generated ResolvedColor
   */
  public static ResolvedColor random() {
    final int r = RANDOM.nextInt(MAX_RGB_VALUE);
    final int g = RANDOM.nextInt(MAX_RGB_VALUE);
    final int b = RANDOM.nextInt(MAX_RGB_VALUE);
    final String hex = String.format("#%02X%02X%02X", r, g, b);
    return new ResolvedColor("Random (" + hex + ")", hex, Color.of(r, g, b));
  }
}
