package org.tiogasolutions.identity.client.core;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;

@Test
public class PubLinkTest {

    private final JsonTranslator translator= new TiogaJacksonTranslator();

    public void testTranslation() throws Exception {

        PubLink oldLink = PubLink.create("self", "http://example.com/");

        String json = translator.toJson(oldLink);
        PubLink newLink = translator.fromJson(PubLink.class, json);

        Assert.assertEquals(oldLink.getHref(), newLink.getHref());
        Assert.assertEquals(oldLink.getRel(), newLink.getRel());
    }
}