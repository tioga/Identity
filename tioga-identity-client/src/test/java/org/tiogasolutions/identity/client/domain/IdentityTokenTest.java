package org.tiogasolutions.identity.client.domain;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

@Test
public class IdentityTokenTest {

    private final JsonTranslator translator= new TiogaJacksonTranslator();

    public void testTranslation() throws Exception {

        PubLinks links = new PubLinks();

        IdentityToken oldToken = new IdentityToken(
                new PubStatus(HttpStatusCode.OK),
                links,
                "token-name",
                "some-domain",
                "fjlkaj34l23jms42lk3js4m2lk34j");

        String json = translator.toJson(oldToken);
        IdentityToken newToken = translator.fromJson(IdentityToken.class, json);

        Assert.assertEquals(oldToken.getDomainName(), newToken.getDomainName());
        Assert.assertEquals(oldToken.getTokenName(), newToken.getTokenName());
        Assert.assertEquals(oldToken.getAuthorizationToken(), newToken.getAuthorizationToken());
    }
}