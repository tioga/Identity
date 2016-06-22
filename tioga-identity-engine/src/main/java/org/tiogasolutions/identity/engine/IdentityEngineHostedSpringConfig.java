package org.tiogasolutions.identity.engine;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
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
import org.tiogasolutions.identity.kernel.domain.TenantProfileEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Collections;
import java.util.List;

@Profile("hosted")
@Configuration
public class IdentityEngineHostedSpringConfig {

    // By all rights this is just silly... but, when an app has extra modules, as is the case with the Push server,
    // this helps ensure that both the ObjectMapper (used by JAX-RS) and the CouchDatabase is configured the same.
    private List<Module> jacksonModule = Collections.singletonList(new TiogaJacksonModule());

//    @Bean
//    public Notifier notifier(@Value("${identity.notifyCouchUrl}") String url,
//                             @Value("${identity.notifyCouchUsername}") String username,
//                             @Value("${identity.notifyCouchPassword}") String password,
//                             @Value("${identity.notifyCouchDatabaseName}") String databaseName) {
//
//        CouchNotificationSender sender = new CouchNotificationSender(url, databaseName, username, password);
//        return new Notifier(sender);
//    }
    @Bean
    public Notifier notifier() {
        // CouchNotificationSender would be the preferred choice, but...
        LoggingNotificationSender sender = new LoggingNotificationSender();
        return new Notifier(sender);
    }

    @Bean
    public ThymeleafMessageBodyWriterConfig thymeleafMessageBodyWriterConfig() {
        ThymeleafMessageBodyWriterConfig config = new ThymeleafMessageBodyWriterConfig();
        config.setPathPrefix("/tioga-identity-engine/bundled/");
        config.setPathSuffix(".html");
        config.setCacheable(true);
        return config;
    }

    @Bean
    public ExecutionManager<TenantProfileEo> executionManager() {
        return new ExecutionManager<>();
    }

    @Bean
    public TenantStore accountStore() {
        return new TenantStore();
    }

    @Bean
    public IdentityRequestFilterDomainResolver identityRequestFilterDomainResolver() {
        return new IdentityRequestFilterDomainResolver();
    }

    @Bean
    public IdentityAuthenticationResponseFactory identityAuthenticationResponseFactory() {
        return new IdentityAuthenticationResponseFactory();
    }

    @Bean
    public StandardRequestFilterConfig standardRequestFilterConfig(TenantStore tenantStore) {
        StandardRequestFilterConfig config = new StandardRequestFilterConfig();

        // The first list is everything that is unsecured.
        config.registerAuthenticator(AnonymousRequestFilterAuthenticator.SINGLETON,
                "",             // home page
                "^js/.*",       // any javascript
                "^css/.*",      // any css file
                "^images/.*",   // any image
                "^favicon.ico",
                "^application.wadl",
                "^api"
        );

        config.registerAuthenticator(new IdentityTokenRequestFilterAuthenticator(tenantStore), ".*");

        return config;
    }

    @Bean
    public StandardResponseFilterConfig standardResponseFilterConfig() {
        StandardResponseFilterConfig config = new StandardResponseFilterConfig();
        config.getExtraHeaders().put(StandardResponseFilterConfig.P3P, "CP=\"The Identity App does not have a P3P policy.\"");
        return config;
    }

    @Bean
    BundledStaticContentReader bundledStaticContentReader() {
        return new BundledStaticContentReader("/tioga-identity-engine/bundled/");
    }

    @Bean
    public GrizzlyServerConfig grizzlyServerConfig(@Value("${identity.hostName}") String hostName,
                                                   @Value("${identity.port}") int port,
                                                   @Value("${identity.shutdownPort}") int shutdownPort,
                                                   @Value("${identity.context}") String context,
                                                   @Value("${identity.toOpenBrowser}") boolean toOpenBrowser) {

        GrizzlyServerConfig config = new GrizzlyServerConfig();
        config.setHostName(hostName);
        config.setPort(port);
        config.setShutdownPort(shutdownPort);
        config.setContext(context);
        config.setToOpenBrowser(toOpenBrowser);
        return config;
    }

    @Bean
    public CouchServersConfig couchServersConfig(@Value("${identity.couchUrl}") String url,
                                                 @Value("${identity.couchUsername}") String username,
                                                 @Value("${identity.couchPassword}") String password,
                                                 @Value("${identity.couchDatabaseName}") String databaseName) {

        return new CouchServersConfig(url, databaseName, username, password, jacksonModule);
    }

    @Bean
    public DefaultCouchServer defaultCouchServer(CouchServersConfig config) {
        return new DefaultCouchServer(config.toCouchSetup());
    }

    @Bean
    public ResourceConfig resourceConfig(ApplicationContext applicationContext) {
        StandardApplication application = new StandardApplication();
        ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
        resourceConfig.property("contextConfig", applicationContext);
        resourceConfig.packages("org.tiogasolutions.identity");
        return resourceConfig;
    }

    @Bean
    public StandardObjectMapper objectMapper() {
        return new StandardObjectMapper(jacksonModule, Collections.emptyList());
    }

    @Bean
    public TiogaJacksonTranslator tiogaJacksonTranslator(ObjectMapper objectMapper) {
        return new TiogaJacksonTranslator(objectMapper);
    }

    @Bean
    public GrizzlyServer grizzlyServer(GrizzlyServerConfig grizzlyServerConfig, ResourceConfig resourceConfig) {
        return new GrizzlyServer(grizzlyServerConfig, resourceConfig);
    }
}
