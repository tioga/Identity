package org.tiogasolutions.identity.engine;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.tiogasolutions.app.standard.StandardApplication;
import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.app.standard.jackson.StandardObjectMapper;
import org.tiogasolutions.app.standard.jaxrs.auth.AnonymousRequestFilterAuthenticator;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterConfig;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardResponseFilterConfig;
import org.tiogasolutions.app.standard.readers.MockContentReader;
import org.tiogasolutions.app.standard.session.DefaultSessionStore;
import org.tiogasolutions.app.standard.session.SessionStore;
import org.tiogasolutions.app.standard.view.thymeleaf.ThymeleafMessageBodyWriterConfig;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.identity.engine.mock.IdentityRequestFilterDomainResolver;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.identity.engine.kernel.CouchServersConfig;
import org.tiogasolutions.identity.engine.mock.AccountStore;
import org.tiogasolutions.identity.engine.mock.IdentityAuthenticationResponseFactory;

import java.util.Collections;
import java.util.List;

@Profile("test")
@Configuration
@PropertySource("classpath:/tioga-identity-engine/spring-test.properties")
public class IdentityEngineTestSpringConfig {

    // By all rights this is just silly... but, when an app has extra modules, as is the case with the Push server,
    // this helps ensure that both the ObjectMapper (used by JAX-RS) and the CouchDatabase is configured the same.
    private static final List<Module> jacksonModule = Collections.singletonList(new TiogaJacksonModule());

    @Bean
    public Notifier notifier() {
        return new Notifier(new LoggingNotificationSender());
    }

    @Bean
    public ThymeleafMessageBodyWriterConfig thymeleafMessageBodyWriterConfig() {
        ThymeleafMessageBodyWriterConfig config = new ThymeleafMessageBodyWriterConfig();
        config.setPathPrefix("/tioga-identity-engine/bundled");
        config.setPathSuffix(".html");
        config.setCacheable(true);
        return config;
    }

    @Bean
    public SessionStore sessionStore() {
        return new DefaultSessionStore(10*60*60*1000, "session-id");
    }

    @Bean
    public ExecutionManager executionManager() {
        return new ExecutionManager();
    }

    @Bean
    public AccountStore accountStore() {
        return new AccountStore();
    }
    @Bean
    public IdentityRequestFilterDomainResolver identityRequestFilterDomainResolver(AccountStore accountStore, SessionStore sessionStore) {
        return new IdentityRequestFilterDomainResolver(accountStore, sessionStore);
    }

    @Bean
    public StandardResponseFilterConfig standardResponseFilterConfig() {
        return new StandardResponseFilterConfig();
    }

    @Bean
    public IdentityAuthenticationResponseFactory identityAuthenticationResponseFactory(AccountStore accountStore, SessionStore sessionStore) {
        return new IdentityAuthenticationResponseFactory(accountStore, sessionStore);
    }

    @Bean
    public StandardRequestFilterConfig standardRequestFilterConfig() {
        StandardRequestFilterConfig config = new StandardRequestFilterConfig();
        config.registerAuthenticator(AnonymousRequestFilterAuthenticator.SINGLETON, ".*");
        return config;
    }

    @Bean
    public DefaultCouchServer defaultCouchServer(CouchServersConfig config) {
        return new DefaultCouchServer(config.toCouchSetup());
    }

    @Bean
    public StandardObjectMapper objectMapper() {
        return new StandardObjectMapper(jacksonModule, Collections.emptyList());
    }

    @Bean
    public MockContentReader mockContentReader() {
        return new MockContentReader();
    }

    @Bean
    public StandardApplication standardApplication() {
        return new StandardApplication();
    }

    @Bean
    public CouchServersConfig couchServersConfig() {
        return new CouchServersConfig("http://localhost:5984",
                "test-identity",
                "test-user",
                "test-user",
                jacksonModule);
    }
}
