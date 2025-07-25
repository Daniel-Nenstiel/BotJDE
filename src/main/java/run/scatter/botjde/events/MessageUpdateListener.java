package run.scatter.botjde.events;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.Director;

@Service
public class MessageUpdateListener extends MessageListener implements EventListener<MessageUpdateEvent> {

    public MessageUpdateListener(Director director) {
        super(director);
    }

    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
        return Mono.just(event)
           .filter(MessageUpdateEvent::isContentChanged)
           .flatMap(MessageUpdateEvent::getMessage)
           .flatMap(super::processCommand);
    }
}
