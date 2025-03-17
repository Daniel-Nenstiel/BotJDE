package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.BaseCommandProcessor;
import run.scatter.botjde.scheduled.puzzle.PuzzleMessage;

@Slf4j
@Component
public class PuzzleProcessor extends BaseCommandProcessor {
  @Autowired
  private PuzzleMessage puzzleMessage;

  @Override
  public String getTrigger() {
    return "!puzzle";
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
