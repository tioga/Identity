package org.tiogasolutions.identity.engine;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.app.standard.StandardApplication;
import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.app.standard.jackson.StandardObjectMapper;
import org.tiogasolutions.app.standard.jaxrs.auth.AnonymousRequestFilterAuthenticator;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterConfig;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardResponseFilterConfig;
import org.tiogasolutions.app.standard.readers.BundledStaticContentReader;
import org.tiogasolutions.app.standard.view.thymeleaf.ThymeleafMessageBodyWriterConfig;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.engine.support.IdentityAuthenticationResponseFactory;
import org.tiogasolutions.identity.engine.support.IdentityRequestFilterDomainResolver;
import org.tiogasolutions.identity.engine.support.IdentityTokenRequestFilterAuthenticator;
import org.tiogasolutions.identity.kernel.CouchServersConfig;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.InMemoryStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Collections;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.$api_v1;
import static org.tiogasolutions.identity.kernel.constants.Paths.$authenticate;

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
