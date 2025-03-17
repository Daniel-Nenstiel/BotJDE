package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.BaseCommandProcessor;

@Slf4j
@Component
public class TestProcessor extends BaseCommandProcessor {
    @Override
    public String getTrigger() {
        return "test";
    }

    @Override
    public Mono<Void> process(String content, String author, Message message) {
        log.info("Info Log Ping");
        log.trace("Trace log ping");
        log.warn("Warn Log Ping");
        log.debug("Debug Log Ping");
        log.error("Error log ping");
        return message.getChannel() // Get the channel from the message
            .flatMap(channel -> channel.createMessage("pong!"))
            .then();
    }
}
