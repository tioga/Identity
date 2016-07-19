package org.tiogasolutions.identity.engine;

import ch.qos.logback.classic.Level;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.tiogasolutions.app.standard.jackson.StandardObjectMapper;
import org.tiogasolutions.app.standard.jaxrs.StandardReaderWriterProvider;
import org.tiogasolutions.dev.common.LogbackUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.client.LiveIdentityClient;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;

import javax.ws.rs.core.Application;

import static java.lang.String.format;

public abstract class AbstractEngineJaxRsTest extends JerseyTestNg.ContainerPerMethodTest {

    protected ConfigurableListableBeanFactory beanFactory;
    protected LiveIdentityClient client;

    @BeforeMethod
    public void autowireTest() throws Exception {
        beanFactory.autowireBean(this);
    }

    @Override
    protected void configureClient(ClientConfig config) {
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
    }


    protected void assertItem(PubItem item, int code, String message) {
        Assert.assertNotNull(item);

        Assert.assertNotNull(item.get_status());
        Assert.assertEquals(item.get_status().getCode(), code);
        Assert.assertEquals(item.get_status().getMessage(), message);

        PubLinks links = item.get_links();
        Assert.assertNotNull(links);

        for (PubLink link : item.get_links().values()) {
            try {
                client.getOptions(link);
            } catch (ApiException e) {
                Assert.fail(format("The link \"%s\" is not valid: %s", link.getRel(), link.getHref()), e);
            }
        }
    }
}
