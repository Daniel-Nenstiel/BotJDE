package run.scatter.botjde.events;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.Director;

@Service
public abstract class MessageListener {

  private final Director director;

  @Autowired
  public MessageListener(Director director) {
    this.director = director;
  }

  public Mono<Void> processCommand(Message eventMessage) {
    return Mono.just(eventMessage)
        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false)) // Ignore bot messages
        .flatMap(message -> director.directCall(
            message.getContent(),
            message.getAuthor().map(User::getUsername).orElse("Unknown"),
            message
        ));
  }
}
