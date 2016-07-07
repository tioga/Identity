package org.tiogasolutions.identity.client.core;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;

@Test
public class PubLinkTest {

    private final JsonTranslator translator= new TiogaJacksonTranslator();

    public void testTranslation() throws Exception {

        PubLink oldLink = new PubLink("self", "http://example.com/", "An Example");

        String json = translator.toJson(oldLink);
        PubLink newLink = translator.fromJson(PubLink.class, json);

        Assert.assertEquals(oldLink.getHref(), newLink.getHref());
        Assert.assertEquals(oldLink.getRel(), newLink.getRel());
        Assert.assertEquals(oldLink.getTitle(), newLink.getTitle());
    }
}