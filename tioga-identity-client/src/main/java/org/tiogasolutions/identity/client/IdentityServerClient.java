package org.tiogasolutions.identity.client;

import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.client.domain.IdentityToken;

import javax.ws.rs.core.Response;
import java.util.List;

public interface IdentityServerClient {

    IdentityToken authenticate(String domainName, String username, String password);

    <T extends PubItem> T follow(String rel, Class<T> type);

    IdentityInfo getInfo();

    List<String> getOptions(PubLink link);
}
