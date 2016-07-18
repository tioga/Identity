package org.tiogasolutions.identity.client;

import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.domain.AuthenticationRequest;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.client.domain.IdentityToken;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class LiveIdentityServerClient implements IdentityServerClient {

    private final NewSimpleRestClient client;
    private Response lastResponse;

    public LiveIdentityServerClient(String url) {
        TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
        TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
        client = new NewSimpleRestClient(translator, url) {};
    }

    public SimpleRestClient getClient() {
      return client;
    }

    @Override
    public IdentityInfo getInfo() {
        return translate(IdentityInfo.class, client.get(Response.class, "/"));
    }

    @Override
    public IdentityToken authenticate(String domainName, String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest("spending-fyi", "spending-client", "password-123");

        IdentityToken token = translate(IdentityToken.class, client.post(Response.class, "/authenticate", request));

        client.setAuthorization(() -> "Token " + token.getAuthorizationToken());

        return token;
    }

    @Override
    public <T extends PubItem> T follow(String rel, Class<T> type) {

        if (lastResponse == null) {
            String msg = "Links cannot be followed until at least on request has been made.";
            throw new UnsupportedOperationException(msg);
        }

        Link link = lastResponse.getLink(rel);

        if (link == null) {
            List<String> names = lastResponse.getLinks().stream().map(Link::getRel).collect(Collectors.toList());
            String msg = String.format("The link \"%s\" does not exist but found %s", rel, names);
            throw new IllegalArgumentException(msg);
        }

        String subUrl = getSubUrl(link.getUri());
        return translate(type, client.get(Response.class, subUrl));
    }

    @Override
    public List<String> getOptions(PubLink link) {
        String subUrl = getSubUrl(link.getHref());
        return client.getOptions(subUrl);
    }

    public <T> T translate(Class<T> type, Response response) {
        lastResponse = response;
        return client.translateResponse(type, response);
    }

    private String getSubUrl(Object path) {
        int len = client.getRootUrl().length();
        return path.toString().substring(len);
    }
}
