package run.scatter.botjde.config;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.scatter.botjde.events.EventListener;

import java.util.List;

@Configuration
public class BotConfiguration {
    private static final Logger log = LoggerFactory.getLogger( BotConfiguration.class );

    @Value("${token}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = null;

        try {
            client = DiscordClientBuilder.create(token)
              .build()
              .gateway()
              .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.GUILD_MEMBERS)))
              .login()
              .block();

            for(EventListener<T> listener : eventListeners) {
              assert client != null;
              client.on(listener.getEventType())
                  .flatMap(listener::execute)
                  .onErrorResume(listener::handleError)
                  .subscribe();
            }
        } catch ( Exception exception ) {
            log.error( "Be sure to use a valid bot token!", exception );
        }
        return client;
    }
}
