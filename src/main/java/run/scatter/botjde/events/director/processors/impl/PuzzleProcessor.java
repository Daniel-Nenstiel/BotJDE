package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;
import run.scatter.botjde.scheduled.puzzle.PuzzleMessage;

@Component
public class PuzzleProcessor implements CommandProcessor {

  private static final Logger log = LoggerFactory.getLogger(PuzzleProcessor.class);

  @Autowired
  private PuzzleMessage puzzleMessage;

  @Override
  public boolean supports(String command) {
    return "!puzzle".equalsIgnoreCase(command);
  }

  @Override
  public Mono<Void> process(String content, String author, Message message) {
    log.info("Puzzle Command Called");

    String puzzleMessageContent = puzzleMessage.getDefaultPuzzleMessage();

    return message.getChannel()
        .flatMap(channel -> channel.createMessage(puzzleMessageContent))
        .then();
  }
}
