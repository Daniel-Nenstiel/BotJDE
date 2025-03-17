package run.scatter.botjde.events.director.processors;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class BaseCommandProcessor implements CommandProcessor{

  public abstract String getTrigger();

  @Override
  public boolean supports(String command) {
    return getTrigger().equalsIgnoreCase(command);
  }

  public abstract Mono<Void> process(String content, String author, Message message);
}
