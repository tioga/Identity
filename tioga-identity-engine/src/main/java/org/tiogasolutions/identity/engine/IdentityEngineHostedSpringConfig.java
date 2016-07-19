package org.tiogasolutions.identity.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.identity.kernel.store.InMemoryStore;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;

@Profile("hosted")
@Configuration
public class IdentityEngineHostedSpringConfig {

    @Bean
    public Notifier notifier() {
        // CouchNotificationSender would be the preferred choice, but...
        LoggingNotificationSender sender = new LoggingNotificationSender();
        return new Notifier(sender);
    }

    @Bean
    public InMemoryStore inMemoryStore() {
        return new InMemoryStore();
    }
}
