package org.tiogasolutions.identity.client;

import org.testng.annotations.Test;
import org.tiogasolutions.identity.client.domain.IdentityDomain;
import org.tiogasolutions.identity.client.domain.IdentityToken;

import static org.testng.Assert.assertEquals;

@Test
public class LiveIdentityServerClientTest {

    public void testClient() throws Exception {
        String url ="http://localhost:39023";
        IdentityServerClient client = new LiveIdentityServerClient(url);

        IdentityToken token = client.authenticate("spending-fyi", "spending-client", "password-123");
        assertEquals(token.getDomainName(), "spending-fyi");
        assertEquals(token.getTokenName(), "spending-client");

        IdentityDomain domain = client.follow("me", IdentityDomain.class);
        assertEquals(domain.getDomainName(), "spending-fyi");
    }
}