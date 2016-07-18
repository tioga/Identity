package org.tiogasolutions.identity.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.tiogasolutions.identity.kernel.store.InMemoryStore;
import org.tiogasolutions.lib.spring.TiogaPropertyPlaceholderConfigurer;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

@Profile("test")
@Configuration
@PropertySource("classpath:/tioga-identity-engine/spring-test.properties")
public class IdentityEngineTestSpringConfig {

    @Bean
    public Notifier notifier() {
        return new Notifier(new LoggingNotificationSender());
    }

    @Bean
    public InMemoryStore inMemoryStore() {
        return new InMemoryStore();
    }

    @Bean
    public TiogaPropertyPlaceholderConfigurer tiogaPropertyPlaceholderConfigurer() throws Exception {
        return new TiogaPropertyPlaceholderConfigurer("TIOGA_SECRET_PROPERTIES_FILE", "classpath:/tioga-identity-engine/spring.properties");
    }
}
