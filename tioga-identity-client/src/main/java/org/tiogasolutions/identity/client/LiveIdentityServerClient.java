package org.tiogasolutions.identity.client;

import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.domain.AuthenticationRequest;
import org.tiogasolutions.identity.client.domain.IdentityToken;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;

public class LiveIdentityServerClient implements IdentityServerClient {

    private final SimpleRestClient client;
    private PubItem lastItem;

    public LiveIdentityServerClient(String url) {
        TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
        TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
        url += url.endsWith("/") ? "api/v1" : "/api/v1";
        client = new SimpleRestClient(translator, url);
    }

    public LiveIdentityServerClient(SimpleRestClient client) {
      this.client = client;
    }

    public SimpleRestClient getClient() {
      return client;
    }

    @Override
    public IdentityToken authenticate(String domainName, String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest("spending-fyi", "spending-client", "password-123");
        IdentityToken token = client.post(IdentityToken.class, "/authenticate", request);
        client.setAuthorization(() -> "Token " + token.getAuthorizationToken());

        lastItem = token;
        return token;
    }

    @Override
    public <T extends PubItem> T follow(String rel, Class<T> type) {
        // Map<String,Object> headers = new HashMap<>();
        // Map<String,Object> queryMap = new HashMap<>();
        // lastItem = client.get(type, "rel", queryMap, headers);

        PubLink link = lastItem.get_links().getLink(rel);

        int len = client.getRootUrl().length();
        String subUrl = link.getHref().substring(len);

        lastItem = client.get(type, subUrl);

        // noinspection unchecked
        return (T)lastItem;
    }
}
