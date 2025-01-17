package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;

@Component
public class PuzzleProcessor implements CommandProcessor {
  private static final Logger log = LoggerFactory.getLogger(PuzzleProcessor.class);

  @Override
  public boolean supports(String command) {
    return "!puzzle".equalsIgnoreCase(command);
  }

  @Override
  public Mono<Void> process(String content, String author, Message message) {
    log.info("Puzzle Command Called");
    return message.getChannel() // Get the channel from the message
        .flatMap(channel -> channel.createMessage(run.scatter.botjde.scheduled.puzzles.PuzzleMessage.formatMessage()))
        .then();
  }
}
