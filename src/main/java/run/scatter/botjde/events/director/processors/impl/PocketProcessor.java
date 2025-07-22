package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;
import run.scatter.botjde.events.director.service.PocketCommandHandler;

@Slf4j
@Component
public class PocketProcessor implements CommandProcessor {

  @Autowired
  private PocketCommandHandler commandHandler;

  @Override
  public boolean supports(String command) {
      return "!poke".equalsIgnoreCase(command);
    }

  @Override
  public Mono<Void> process(String content, String author, Message message) {
    // Use the handler to get a preformatted response
    String response = commandHandler.handleCommand(content, author);

    // Send the response to Discord
    return message.getChannel()
        .flatMap(channel -> channel.createMessage(response))
        .then();
  }
}
