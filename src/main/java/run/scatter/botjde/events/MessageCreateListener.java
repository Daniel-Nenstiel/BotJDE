package run.scatter.botjde.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.Director;
import run.scatter.botjde.config.AppConfig;

@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    public MessageCreateListener(Director director, AppConfig appConfig) {
        super(director, appConfig);
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }
}
