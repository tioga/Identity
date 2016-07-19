package org.tiogasolutions.identity.engine.resources;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.ApiUnauthorizedException;
import org.tiogasolutions.identity.client.LiveIdentityClient;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.domain.Identity;
import org.tiogasolutions.identity.client.domain.IdentityDomain;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.client.domain.IdentityToken;
import org.tiogasolutions.identity.engine.AbstractEngineJaxRsTest;

import static org.testng.Assert.*;

@Test
public class LiveIdentityClientTest extends AbstractEngineJaxRsTest {

    @BeforeMethod
    public void beforeMethod() throws Exception {
        String url = getBaseUri().toString();
        client = new LiveIdentityClient(url);
    }

    @AfterMethod
    public void afterMethod() throws Exception {
    }

    public void testPermanentRedirects() {
        PubItem item = client.get(PubItem.class, "/");
        assertItem(item, 200, "OK");
        assertEquals(item.get_links().get("self").getHref(), "http://localhost:9998/api/v1");

        item = client.get(PubItem.class, "/api");
        assertItem(item, 200, "OK");
        assertEquals(item.get_links().get("self").getHref(), "http://localhost:9998/api/v1");

        item = client.get(PubItem.class, "/api/v1");
        assertItem(item, 200, "OK");
        assertEquals(item.get_links().get("self").getHref(), "http://localhost:9998/api/v1");

        item = client.get(PubItem.class, "/api/v1/anonymous");
        assertItem(item, 200, "OK");
        assertEquals(item.get_links().get("self").getHref(), "http://localhost:9998/api/v1");
    }

    public void testInfo() {
        IdentityInfo info = client.getInfo();
        assertItem(info, 200, "OK");

        assertNotNull(info.getUpTime().startsWith("0 days, 0 hours, 0 minutes, "), "Found: " + info.getUpTime());
        assertNotNull(info.getUpTime().endsWith(" seconds"), "Found: " + info.getUpTime());
    }

    public void testAnonymousInfo() {
        IdentityInfo info = client.getInfo();
        assertItem(info, 200, "OK");

        assertNotNull(info.getUpTime().startsWith("0 days, 0 hours, 0 minutes, "), "Found: " + info.getUpTime());
        assertNotNull(info.getUpTime().endsWith(" seconds"), "Found: " + info.getUpTime());
    }

    public void testCreateToken() {
        IdentityToken token = client.createToken("spending-fyi", "spending-client", "password-123");
        assertItem(token, 201, "Created");

        assertEquals(token.getDomainName(), "spending-fyi");
        assertEquals(token.getTokenName(), "spending-client");

        assertNotNull(token.getAuthorizationToken());
        assertNotEquals(token.getAuthorizationToken(), "9876543210");

        assertEquals(token.getLink("self").getHref(), "http://localhost:9998/api/v1/me/tokens/spending-client");

        IdentityDomain domain = client.follow(IdentityDomain.class, "me");
        assertEquals(domain.getDomainName(), "spending-fyi");
    }

    public void testAuthenticate() {
        IdentityToken token = client.authenticate("spending-fyi", "spending-client", "password-123");
        assertItem(token, 200, "OK");

        assertEquals(token.getDomainName(), "spending-fyi");
        assertEquals(token.getTokenName(), "spending-client");

        assertNotNull(token.getAuthorizationToken());
        assertEquals(token.getAuthorizationToken(), "9876543210");

        assertEquals(token.getLink("self").getHref(), "http://localhost:9998/api/v1/me/tokens/spending-client");

        IdentityDomain domain = client.follow(IdentityDomain.class, "me");
        assertEquals(domain.getDomainName(), "spending-fyi");
    }

    public void testUnauthenticatedMe() {
        try {
            client.getMe();
            fail("Expected exception");

        } catch (ApiUnauthorizedException e) {
            assertEquals(e.getMessage(), "Unexpected response: 401 Unauthorized");
        }
    }

    public void testMe() {
        client.setAuthorizationToken("9876543210");

        IdentityDomain domain = client.getMe();
        assertItem(domain, 200, "OK");
        assertEquals(domain.getDomainName(), "spending-fyi");
    }

    public void testIdentityByUsername() {
        client.setAuthorizationToken("9876543210");

        Identity identity = client.getIdentityByUsername("angieparr@gmail.com");
        assertTestIdentity(identity);
    }

    public void testIdentityById() {
        client.setAuthorizationToken("9876543210");

        Identity identity = client.getIdentityById("spending-fyi:angieparr@gmail.com");
        assertTestIdentity(identity);
    }

    private void assertTestIdentity(Identity identity) {
        assertItem(identity, 200, "OK");
        assertEquals(identity.getUsername(), "angieparr@gmail.com");
        assertEquals(identity.getPassword(), "password-123");
        assertEquals(identity.getDomainName(), "spending-fyi");
    }
}