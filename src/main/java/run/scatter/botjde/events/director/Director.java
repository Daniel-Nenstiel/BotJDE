package run.scatter.botjde.events.director;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
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

    public Mono<Void> directCall(String content, String author, Message message) {
        final String command = content.split(" ")[0]; // Extract the command
        return processors.stream()
            .filter(processor -> processor.supports(command)) // Find the appropriate processor
            .findFirst()
            .map(processor -> processor.process(content, author, message)) // Pass the Message object
            .orElseGet(Mono::empty);
    }

    public Mono<Void> handleMessage(Message message) {
        return directCall(
            message.getContent(),
            message.getAuthor().map(User::getUsername).orElse("Unknown"),
            message
        );
    }

    public Mono<Void> handleThreadMessage(Message threadMessage) {
        return directCall(
            threadMessage.getContent(),
            threadMessage.getAuthor().map(User::getUsername).orElse("Unknown"),
            threadMessage
        );
    }
}
