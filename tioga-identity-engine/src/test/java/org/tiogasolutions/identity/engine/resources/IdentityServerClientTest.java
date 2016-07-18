package org.tiogasolutions.identity.engine.resources;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.identity.client.IdentityServerClient;
import org.tiogasolutions.identity.client.LiveIdentityServerClient;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.engine.AbstractEngineJaxRsTest;

@Test
public class IdentityServerClientTest extends AbstractEngineJaxRsTest {

    private IdentityServerClient client;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        String url = getBaseUri().toString();
        client = new LiveIdentityServerClient(url);
    }

    @AfterMethod
    public void afterMethod() throws Exception {
    }

    public void testRoot() {
        IdentityInfo info = client.getInfo();
        assertItem(info, 200, "OK");

        Assert.assertNotNull(info.getUpTime().startsWith("0 days, 0 hours, 0 minutes, "), "Found: " + info.getUpTime());
        Assert.assertNotNull(info.getUpTime().endsWith(" seconds"), "Found: " + info.getUpTime());

        PubItem item = client.follow("api", PubItem.class);
        assertItem(item, 200, "OK");
    }

    private void assertItem(PubItem item, int code, String message) {
        Assert.assertNotNull(item);

        Assert.assertNotNull(item.get_status());
        Assert.assertEquals(item.get_status().getCode(), code);
        Assert.assertEquals(item.get_status().getMessage(), message);

        PubLinks links = item.get_links();
        Assert.assertNotNull(links);

        for (PubLink link : item.get_links().values()) {
            // 404 exception thrown if its a bad link.
            client.getOptions(link);
        }
    }
}