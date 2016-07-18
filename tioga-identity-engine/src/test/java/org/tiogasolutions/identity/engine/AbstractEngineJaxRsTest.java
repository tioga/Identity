package org.tiogasolutions.identity.engine;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.tiogasolutions.app.standard.jackson.StandardObjectMapper;
import org.tiogasolutions.app.standard.jaxrs.StandardReaderWriterProvider;
import org.tiogasolutions.dev.common.LogbackUtils;
import org.tiogasolutions.lib.jaxrs.providers.TiogaReaderWriterProvider;

import javax.ws.rs.core.Application;

public class AbstractEngineJaxRsTest extends JerseyTestNg.ContainerPerMethodTest {

    private ConfigurableListableBeanFactory beanFactory;

    @BeforeMethod
    public void autowireTest() throws Exception {
        beanFactory.autowireBean(this);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        String[] names = beanFactory.getBeanDefinitionNames();

        StandardObjectMapper om = beanFactory.getBean(StandardObjectMapper.class);
        config.register(new StandardReaderWriterProvider(om));
    }

    @Override
    protected Application configure() {
        LogbackUtils.initLogback(Level.WARN);

        AnnotationConfigApplicationContext applicationContext;

        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("test");
        applicationContext.scan("org.tiogasolutions.identity");
        applicationContext.refresh();

        // Inject our unit test with any beans.
        beanFactory = applicationContext.getBeanFactory();

        return beanFactory.getBean(ResourceConfig.class);

//    ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
//    resourceConfig.register(SpringLifecycleListener.class);
//    resourceConfig.register(RequestContextFilter.class);
//    resourceConfig.property("contextConfig", applicationContext);
//    resourceConfig.packages("org.tiogasolutions.identity");

//    return resourceConfig;
    }
}
