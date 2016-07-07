package org.tiogasolutions.identity.client;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.domain.IdentityDomain;
import org.tiogasolutions.identity.client.domain.IdentityToken;

@Test
public class LiveIdentityServerClientTest {

    public void testClient() throws Exception {
        String url ="http://localhost:39023";
        IdentityServerClient client = new LiveIdentityServerClient(url);

        IdentityToken token = client.authenticate("spending-fyi", "spending-client", "password-123");
        Assert.assertNotNull(token);
        Assert.assertEquals(token.getDomainName(), "spending-fyi");
        Assert.assertEquals(token.getTokenName(), "spending-client");
        Assert.assertEquals(token.get_status().getCode(), 201);
        Assert.assertEquals(token.get_status().getMessage(), "Created");
        Assert.assertNotNull(token.getAuthorizationToken());
        printLinks(token.get_links());

        IdentityDomain domain = client.follow("me", IdentityDomain.class);
        Assert.assertEquals(domain.getDomainName(), "spending-fyi");
    }

    private void printLinks(PubLinks links) {
        for (PubLink link : links.getItems()) {
            System.out.printf("%s: %s\n", link.getRel(), link.getHref());
        }
    }
}