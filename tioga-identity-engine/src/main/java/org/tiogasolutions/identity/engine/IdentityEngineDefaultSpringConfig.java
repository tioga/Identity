package org.tiogasolutions.identity.engine;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tiogasolutions.app.standard.StandardApplication;
import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.app.standard.jackson.StandardObjectMapper;
import org.tiogasolutions.app.standard.jaxrs.auth.AnonymousRequestFilterAuthenticator;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterConfig;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardResponseFilterConfig;
import org.tiogasolutions.app.standard.readers.BundledStaticContentReader;
import org.tiogasolutions.app.thymeleaf.ThymeleafMessageBodyWriterConfig;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.engine.support.*;
import org.tiogasolutions.identity.kernel.CouchServersConfig;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Collections;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.*;

@Configuration
public class IdentityEngineDefaultSpringConfig {

    private List<Module> jacksonModule = Collections.singletonList(new TiogaJacksonModule());

    @Bean
    public ThymeleafMessageBodyWriterConfig thymeleafMessageBodyWriterConfig() {
        ThymeleafMessageBodyWriterConfig config = new ThymeleafMessageBodyWriterConfig();
        config.setPathPrefix("/tioga-identity-engine/bundled/");
        config.setPathSuffix(".html");
        config.setCacheable(true);
        return config;
    }

    @Bean
    public ExecutionManager<IdentityKernel> executionManager() {
        return new ExecutionManager<>();
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
    public StandardRequestFilterConfig standardRequestFilterConfig(DomainStore domainStore, IdentityStore identityStore) {
        StandardRequestFilterConfig config = new StandardRequestFilterConfig();

        // The first list is everything that is unsecured.
        config.registerAuthenticator(AnonymousRequestFilterAuthenticator.SINGLETON,
                "",             // home page
                "^js/.*",       // any javascript
                "^css/.*",      // any css file
                "^images/.*",   // any image
                "^favicon.ico",
                "^application.wadl"
        );

        config.registerAuthenticator(new UnauthenticatedRequestFilterAuthenticator(domainStore, identityStore),
                "^"+ $api,
                "^"+ $api + "/",
                "^"+ $api_v1,
                "^"+ $api_v1 + "/",
                "^"+ $api_v1 + "/" + $anonymous + ".*");

        config.registerAuthenticator(new IdentityTokenRequestFilterAuthenticator(domainStore, identityStore),   "^"+ $api_v1 + ".*");

        return config;
    }

    @Bean
    public StandardResponseFilterConfig standardResponseFilterConfig() {
        return new StandardResponseFilterConfig();
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
    public ResourceConfig resourceConfig(ApplicationContext applicationContext) {
        StandardApplication application = new StandardApplication();
        application.getClasses().add(RolesAllowedDynamicFeature.class);

        application.getClasses().add(IdentityExceptionMapper.class);

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
