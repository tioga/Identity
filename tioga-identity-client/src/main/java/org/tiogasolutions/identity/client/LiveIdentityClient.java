package org.tiogasolutions.identity.client;

import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.domain.*;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class LiveIdentityClient implements IdentityClient {

    private final NewSimpleRestClient client;
    private Response lastResponse;

    public LiveIdentityClient(String url) {
        TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
        TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
        client = new NewSimpleRestClient(translator, url) {};
    }

    public SimpleRestClient getClient() {
      return client;
    }

    private <T> T translate(Class<T> type, Response response) {
        lastResponse = response;
        return client.translateResponse(type, response);
    }

    private String getSubUrl(Object path) {
        int len = client.getRootUrl().length();
        return path.toString().substring(len);
    }

    @Override
    public void setAuthorizationToken(String token) {
        client.setAuthorization(() -> "Token " + token);
    }

    @Override
    public <T extends PubItem> T follow(Class<T> type, String rel) {

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

    public <T extends PubItem> T get(Class<T> type, String subUrl) {
        return translate(type, client.get(Response.class, subUrl));
    }

    @Override
    public List<String> getOptions(PubLink link) {
        String subUrl = getSubUrl(link.getHref());
        return client.getOptions(subUrl);
    }

    @Override
    public IdentityInfo getInfo() {
        return translate(IdentityInfo.class, client.get(Response.class, "/api/v1/anonymous/info"));
    }

    public IdentityToken createToken(String domainName, String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest(domainName, username, password);
        IdentityToken token = translate(IdentityToken.class, client.post(Response.class, "/api/v1/anonymous/tokens", request));

        client.setAuthorization(() -> "Token " + token.getAuthorizationToken());
        return token;
    }

    @Override
    public IdentityToken authenticate(String domainName, String username, String password) {
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("domainName", domainName);
        queryMap.put("username", username);
        queryMap.put("password", password);

        lastResponse = client.get(Response.class, "/api/v1/anonymous/authentication", queryMap);
        IdentityToken token = client.translateResponse(IdentityToken.class, lastResponse);

        client.setAuthorization(() -> "Token " + token.getAuthorizationToken());
        return token;
    }

    @Override
    public IdentityDomain getMe() {
        return translate(IdentityDomain.class, client.get(Response.class, "/api/v1/me"));
    }

    @Override
    public Identity getIdentityByUsername(String username) {
        String path = String.format("/api/v1/me/identities/by-username/%s", username);
        return translate(Identity.class, client.get(Response.class, path));
    }

    @Override
    public Identity getIdentityById(String id) {
        String path = String.format("/api/v1/me/identities/by-id/%s", id);
        return translate(Identity.class, client.get(Response.class, path));
    }
}
