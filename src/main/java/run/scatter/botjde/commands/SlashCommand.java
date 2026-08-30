package run.scatter.botjde.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

/**
 * Defines a Discord slash command.
 * <p>
 * To add a new command, create a Spring {@code @Component} implementing this interface.
 * It will be automatically registered with Discord at startup and routed by
 * {@link run.scatter.botjde.events.SlashCommandListener}.
 */
public interface SlashCommand {

  /**
   * The command name as registered with Discord (e.g. "puzzle" for /puzzle).
   * Must be lowercase, 1-32 characters, no spaces.
   *
   * @return the name of the slash command
   */
  String getName();

  /**
   * The Discord API definition for this command — name, description, and any options.
   *
   * @return the Discord application command request definition
   */
  ApplicationCommandRequest getCommandDefinition();

  /**
   * Handles an incoming slash command interaction.
   * Must reply or defer within 3 seconds.
   *
   * @param event the Discord interaction event
   * @return a Mono representing completion of the interaction response
   */
  Mono<Void> handle(ChatInputInteractionEvent event);
}
