package org.tiogasolutions.identity.client;

import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.domain.Identity;
import org.tiogasolutions.identity.client.domain.IdentityDomain;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.client.domain.IdentityToken;

import javax.ws.rs.core.Response;
import java.util.List;

public interface IdentityClient {

    IdentityToken authenticate(String domainName, String username, String password);

    <T extends PubItem> T follow(Class<T> type, String rel);

    IdentityInfo getInfo();

    List<String> getOptions(PubLink link);

    public IdentityDomain getMe();

    void setAuthorizationToken(String token);

    Identity getIdentityByUsername(String username);

    Identity getIdentityById(String id);
}
