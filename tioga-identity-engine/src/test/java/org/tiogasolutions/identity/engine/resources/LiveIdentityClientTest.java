package org.tiogasolutions.identity.engine.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.ApiForbiddenException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.exceptions.ApiUnauthorizedException;
import org.tiogasolutions.identity.client.LiveIdentityClient;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.domain.*;
import org.tiogasolutions.identity.engine.AbstractEngineJaxRsTest;
import org.tiogasolutions.identity.kernel.store.InMemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;
import static org.tiogasolutions.identity.kernel.store.InMemoryStore.*;

@Test
public class LiveIdentityClientTest extends AbstractEngineJaxRsTest {

    @Autowired
    private InMemoryStore inMemoryStore;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        inMemoryStore.reset();

        String url = getBaseUri().toString();
        client = new LiveIdentityClient(url);
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
        IdentityToken token = client.createToken("acme-systems", "acme-systems-client", "password-123");
        assertItem(token, 201, "Created");

        assertEquals(token.getDomainName(), "acme-systems");
        assertEquals(token.getTokenName(), "acme-systems-client");

        assertNotNull(token.getAuthorizationToken());
        assertNotEquals(token.getAuthorizationToken(), ACME_SYSTEM_CLIENT_TOKEN);

        assertEquals(token.getLink("self").getHref(), "http://localhost:9998/api/v1/me/tokens/acme-systems-client");

        IdentityDomain domain = client.follow(IdentityDomain.class, "me");
        assertEquals(domain.getDomainName(), "acme-systems");
    }

    public void testAuthenticate() {
        IdentityToken token = client.authenticate("acme-systems", "acme-systems-client", "password-123");
        assertItem(token, 200, "OK");

        assertEquals(token.getDomainName(), "acme-systems");
        assertEquals(token.getTokenName(), "acme-systems-client");

        assertNotNull(token.getAuthorizationToken());
        assertEquals(token.getAuthorizationToken(), ACME_SYSTEM_CLIENT_TOKEN);

        assertEquals(token.getLink("self").getHref(), "http://localhost:9998/api/v1/me/tokens/acme-systems-client");

        IdentityDomain domain = client.follow(IdentityDomain.class, "me");
        assertEquals(domain.getDomainName(), "acme-systems");
    }

    public void testUnauthenticatedMe() {
        try {
            client.getMe();
            fail("Expected exception");

        } catch (ApiUnauthorizedException e) {
            assertEquals(e.getMessage(), "Invalid authorization token.");
        }
    }

    public void testMe() {
        client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);

        IdentityDomain domain = client.getMe();
        assertItem(domain, 200, "OK");
        assertEquals(domain.getDomainName(), "acme-systems");
    }

    public void testIdentityByUsername() {
        client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);

        Identity identity = client.getIdentityByUsername("wile.e.coyote@acme-systems.com");
        assertTestIdentity(identity);
    }

    public void testIdentityById() {
        client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);

        Identity identity = client.getIdentityById("acme-systems:wile.e.coyote@acme-systems.com");
        assertTestIdentity(identity);
    }

    public void testIdentityByUsernameNotFound() {
        try {
            client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);
            client.getIdentityByUsername("donald.duck@disney.com");
            fail("Expected not-found");

        } catch (ApiNotFoundException e) {
            assertEquals(e.getMessage(), "The specified identity was not found.");
        }
    }

    public void testIdentityByIdNotFound() {
        try {
            client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);
            client.getIdentityById("acme-systems:donald.duck@disney.com");
            fail("Expected not-found");

        } catch (ApiNotFoundException e) {
            assertEquals(e.getMessage(), "The specified identity was not found.");
        }
    }

    public void testIdentityByUsernameOtherDomain() {
        // Make sure Donald exists with the correct credentials
        client.setAuthorizationToken(DISNEY_CLIENT_TOKEN);
        client.getIdentityByUsername("donald.duck@disney.com");

        try {
            client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);
            client.getIdentityByUsername("donald.duck@disney.com");
            fail("Expected not-found");

        } catch (ApiNotFoundException e) {
            assertEquals(e.getMessage(), "The specified identity was not found.");
        }
    }

    public void testIdentityByIdOtherDomain() {
        // Make sure Donald exists with the correct credentials
        client.setAuthorizationToken(DISNEY_CLIENT_TOKEN);
        client.getIdentityById("disney:donald.duck@disney.com");

        try {
            client.setAuthorizationToken(ACME_SYSTEM_CLIENT_TOKEN);
            client.getIdentityById("disney:donald.duck@disney.com");
            fail("Expected not-found");

        } catch (ApiForbiddenException e) {
            assertEquals(e.getMessage(), "The specified identity was not found.");
        }
    }

    private void assertTestIdentity(Identity identity) {
        assertItem(identity, 200, "OK");
        assertEquals(identity.getUsername(), "wile.e.coyote@acme-systems.com");
        assertEquals(identity.getPassword(), "password-123");
        assertEquals(identity.getDomainName(), "acme-systems");

        IdentityRole role;
        IdentityGrant grant;
        List<String> permissions;
        List<IdentityGrant> grants = new ArrayList<>(identity.getGrants());
        List<IdentityRole> roles = new ArrayList<>(identity.getRoles());

        assertNotNull(grants);
        assertEquals(grants.size(), 2);

        grant = grants.get(0);
        assertEquals(grant.getRealmName(), "Public");
        permissions = new ArrayList<>(grant.getPermissions());
        assertEquals(permissions.size(), 2);
        assertEquals(permissions.get(0), "CANCEL");
        assertEquals(permissions.get(1), "SEND");

        grant = grants.get(1);
        assertEquals(grant.getRealmName(), "Secure");
        permissions = new ArrayList<>(grant.getPermissions());
        assertEquals(permissions.size(), 4);
        assertEquals(permissions.get(0), "CANCEL");
        assertEquals(permissions.get(1), "DELETE");
        assertEquals(permissions.get(2), "REFUND");
        assertEquals(permissions.get(3), "SEND");

        assertEquals(roles.size(), 3);

        role = roles.get(0);
        assertEquals(role.getRoleName(), "Guest");
        permissions = new ArrayList<>(role.getPermissions());
        assertEquals(permissions.size(), 0);


        role = roles.get(1);
        assertEquals(role.getRoleName(), "User");
        permissions = new ArrayList<>(role.getPermissions());
        assertEquals(permissions.size(), 2);
        assertEquals(permissions.get(0), "CANCEL");
        assertEquals(permissions.get(1), "SEND");


        role = roles.get(2);
        assertEquals(role.getRoleName(), "Administrator");
        permissions = new ArrayList<>(role.getPermissions());
        assertEquals(permissions.size(), 2);
        assertEquals(permissions.get(0), "DELETE");
        assertEquals(permissions.get(1), "REFUND");
    }
}