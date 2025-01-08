package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;

@Component
public class TestProcessor implements CommandProcessor {
    private static final Logger log = LoggerFactory.getLogger(TestProcessor.class);

    @Override
    public boolean supports(String command) {
        return "!test".equalsIgnoreCase(command);
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
