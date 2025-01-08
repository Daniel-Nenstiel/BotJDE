package run.scatter.botjde.events.director;

import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;

import java.util.List;

@Component
public class Director {
  private final List<CommandProcessor> processors;

  public Director(List<CommandProcessor> processors) {
    this.processors = processors;
  }

  public Mono<Void> directCall(String content, String author, Mono<MessageChannel> channelMono) {
    String command = content.split(" ")[0]; // Extract the command
    if( !command.startsWith("!")){ return Mono.empty(); }

    return processors.stream()
        .filter(processor -> processor.supports(command)) // Find the correct processor
        .findFirst()
        .map(processor -> processor.process(content, author, channelMono)) // Pass the Mono<MessageChannel>
        .orElseGet(() -> {
          System.out.println("No processor found for command: " + command);
          return Mono.empty();
        });
  }
}