package run.scatter.botjde.events;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.Director;
import run.scatter.botjde.config.AppConfig;
import run.scatter.botjde.entity.Server;

@Service
public abstract class MessageListener {

  private final Director director;
  private final AppConfig appConfig;

  @Autowired
  public MessageListener(Director director, AppConfig appConfig) {
    this.director = director;
    this.appConfig = appConfig;
  }

  public Mono<Void> processCommand(Message eventMessage) {
  return Mono.just(eventMessage)
    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
    .filter(message -> message.getGuildId()
      .map(gid -> appConfig.getServers().stream()
        .map(Server::getId)
        .anyMatch(gid::equals))
      .orElse(true))
    .flatMap(message -> director.directCall(
      message.getContent(),
      message.getAuthor().map(User::getUsername).orElse("Unknown"),
      message
    ));
  }
}
