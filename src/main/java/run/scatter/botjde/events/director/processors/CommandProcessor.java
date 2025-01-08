package run.scatter.botjde.events.director.processors;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface CommandProcessor {
    boolean supports(String command);

    Mono<Void> process(String content, String author, Message message);
}
