package org.tiogasolutions.identity.client.domain;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubLinks;

@Test
public class IdentityInfoTest {

    private final JsonTranslator translator = new TiogaJacksonTranslator();

    public void testTranslation() throws Exception {
        PubLinks oldLinks = PubLinks.empty();
        oldLinks.add("self", "http://self.com");
        oldLinks.add("google", "http://google.com");
        oldLinks.add("whatever", "http://whatever.com");

        IdentityInfo oldInfo = new IdentityInfo(HttpStatusCode.OK, oldLinks, 123234234);

        String json = translator.toJson(oldInfo);
        IdentityInfo newInfo = translator.fromJson(IdentityInfo.class, json);
        PubLinks newLinks = newInfo.get_links();

        Assert.assertEquals(newInfo.getUpTime(), oldInfo.getUpTime());
        Assert.assertEquals(newInfo.get_status().getCode(), oldInfo.get_status().getCode());
        Assert.assertEquals(newInfo.get_status().getMessage(), oldInfo.get_status().getMessage());

        Assert.assertEquals(oldLinks.getItems().size(), newLinks.getItems().size());
    }
}
