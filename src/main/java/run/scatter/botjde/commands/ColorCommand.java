package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.model.DiscordColorPalette.ResolvedColor;
import run.scatter.botjde.color.model.DiscordColorPalette.Swatch;
import run.scatter.botjde.color.service.ColorService;

import java.util.List;

/**
 * Slash command /color for managing username colors (set, remove, random, list).
 */
@Slf4j
@Component
public class ColorCommand implements SlashCommand {

  private static final int FORBIDDEN_STATUS_CODE = 403;

  private final ColorService colorService;

  public ColorCommand(ColorService colorService) {
    this.colorService = colorService;
  }

  @Override
  public String getName() {
    return "color";
  }

  @Override
  public ApplicationCommandRequest getCommandDefinition() {
    // Build choices for 16 default swatch colors
    final List<ApplicationCommandOptionChoiceData> choices = colorService.getPalette().stream()
        .map(swatch -> (ApplicationCommandOptionChoiceData) ApplicationCommandOptionChoiceData.builder()
            .name(swatch.displayName() + " (" + swatch.hex() + ")")
            .value(swatch.id())
            .build())
        .toList();

    return ApplicationCommandRequest.builder()
        .name(getName())
        .description("Customize your Discord username color")
        .addOption(ApplicationCommandOptionData.builder()
            .name("set")
            .description("Set your username color using a preset name or hex code")
            .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
            .addOption(ApplicationCommandOptionData.builder()
                .name("color")
                .description("Preset swatch name or custom hex code (e.g. #FF5733)")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .choices(choices)
                .build())
            .build())
        .addOption(ApplicationCommandOptionData.builder()
            .name("remove")
            .description("Remove your custom username color")
            .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
            .build())
        .addOption(ApplicationCommandOptionData.builder()
            .name("random")
            .description("Assign a random vibrant username color")
            .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
            .build())
        .addOption(ApplicationCommandOptionData.builder()
            .name("list")
            .description("List available default color swatches")
            .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
            .build())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.deferReply()
        .then(routeCommand(event));
  }

  private Mono<Void> routeCommand(ChatInputInteractionEvent event) {
    final List<ApplicationCommandInteractionOption> options = event.getOptions();
    if (options.isEmpty()) {
      return event.editReply("Please specify a subcommand: `/color set <color>`, `/color remove`, `/color random`, or `/color list`.").then();
    }

    final ApplicationCommandInteractionOption subcommand = options.get(0);
    final String subcommandName = subcommand.getName();

    final Member member = event.getInteraction().getMember().orElse(null);
    if (member == null) {
      return event.editReply("This command can only be used inside a Discord server.").then();
    }

    return switch (subcommandName.toLowerCase()) {
      case "set" -> handleSet(event, member, subcommand);
      case "remove" -> handleRemove(event, member);
      case "random" -> handleRandom(event, member);
      case "list" -> handleList(event);
      default -> event.editReply("Unknown subcommand `" + subcommandName + "`.").then();
    };
  }

  private Mono<Void> handleSet(ChatInputInteractionEvent event, Member member, ApplicationCommandInteractionOption subcommand) {
    final String colorInput = subcommand.getOption("color")
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString)
        .orElse("");

    if (colorInput.isBlank()) {
      return event.editReply("Please specify a color name or hex code (e.g. `/color set teal` or `/color set #FF5733`).").then();
    }

    return colorService.setColor(member, colorInput)
        .flatMap(resolved -> {
          final EmbedCreateSpec embed = EmbedCreateSpec.builder()
              .title("🎨 Color Updated!")
              .color(resolved.color())
              .description("Your username color has been set to **" + resolved.name() + "** (`" + resolved.hex() + "`).")
              .build();
          return event.editReply().withEmbeds(embed).then();
        })
        .onErrorResume(e -> handleColorError(event, e));
  }

  private Mono<Void> handleRemove(ChatInputInteractionEvent event, Member member) {
    return colorService.removeColor(member)
        .then(event.editReply("✨ Custom color removed. Your username color is now reset to default.").then())
        .onErrorResume(e -> handleColorError(event, e));
  }

  private Mono<Void> handleRandom(ChatInputInteractionEvent event, Member member) {
    return colorService.setRandomColor(member)
        .flatMap(resolved -> {
          final EmbedCreateSpec embed = EmbedCreateSpec.builder()
              .title("🎲 Random Color Assigned!")
              .color(resolved.color())
              .description("Your username color has been set to **" + resolved.hex() + "**.")
              .build();
          return event.editReply().withEmbeds(embed).then();
        })
        .onErrorResume(e -> handleColorError(event, e));
  }

  private Mono<Void> handleList(ChatInputInteractionEvent event) {
    final StringBuilder description = new StringBuilder("Here are the **16 default Discord role colors**:\n\n");

    for (Swatch swatch : colorService.getPalette()) {
      description.append(swatch.emoji())
          .append(" **")
          .append(swatch.displayName())
          .append("**: `")
          .append(swatch.hex())
          .append("` (id: `")
          .append(swatch.id())
          .append("`)\n");
    }

    description.append("\n💡 *You can also use any custom 6-digit hex code: `/color set color:#FF5733`*");

    final EmbedCreateSpec embed = EmbedCreateSpec.builder()
        .title("🎨 Discord Color Swatches")
        .color(Color.BLUE)
        .description(description.toString())
        .build();

    return event.editReply().withEmbeds(embed).then();
  }

  private Mono<Void> handleColorError(ChatInputInteractionEvent event, Throwable throwable) {
    log.error("Color command error: {}", throwable.getMessage(), throwable);

    if (throwable instanceof IllegalArgumentException) {
      return event.editReply("❌ " + throwable.getMessage()).then();
    }

    if (throwable instanceof ClientException clientException && clientException.getStatus().code() == FORBIDDEN_STATUS_CODE) {
      return event.editReply(
          "❌ **Permission Error**: BotJDE cannot manage roles. Please verify that:\n"
              + "1. The bot has the `Manage Roles` permission.\n"
              + "2. The **BotJDE** role is placed higher than color roles in Server Settings -> Roles."
      ).then();
    }

    return event.editReply("❌ An unexpected error occurred: " + throwable.getMessage()).then();
  }
}
