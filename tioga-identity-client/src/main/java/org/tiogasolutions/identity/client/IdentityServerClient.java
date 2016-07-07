package org.tiogasolutions.identity.client;

import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.domain.IdentityToken;

public interface IdentityServerClient {

    IdentityToken authenticate(String domainName, String username, String password);

    <T extends PubItem> T follow(String rel, Class<T> type);
}
