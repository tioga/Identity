package org.tiogasolutions.identity.client.core;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;

@Test
public class PubLinksTest {

    private final JsonTranslator translator= new TiogaJacksonTranslator();

    public void testEmptyTranslation() throws Exception {

        PubLinks oldLinks = new PubLinks();

        String json = translator.toJson(oldLinks);
        System.out.println(json);
        PubLinks newLinks = translator.fromJson(PubLinks.class, json);

        Assert.assertEquals(oldLinks.getItems().isEmpty(), newLinks.getItems().isEmpty());
    }

    public void testTranslation() throws Exception {

        PubLinks oldLinks = new PubLinks();
        oldLinks.add("example", "http://www.example.com", "An Example");
        oldLinks.add("google", "http://www.google.com", "Search Engine");

        String json = translator.toJson(oldLinks);
        System.out.println(json);
        PubLinks newLinks = translator.fromJson(PubLinks.class, json);

        Assert.assertEquals(oldLinks.getItems().isEmpty(), newLinks.getItems().isEmpty());
    }
}