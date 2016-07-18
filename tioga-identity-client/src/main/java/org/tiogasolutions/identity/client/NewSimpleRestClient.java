package org.tiogasolutions.identity.client;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewSimpleRestClient extends SimpleRestClient {

    public NewSimpleRestClient(TiogaJacksonTranslator translator, String url) {
        super(translator, url);
    }

    public List<String> getOptions(String url) {
        Invocation.Builder builder = super.builder(url, Collections.emptyMap(), Collections.emptyMap());
        Response response = builder.options();
        assertResponse(200, response);

        String header = response.getHeaderString("allow");

        if (header == null || StringUtils.isBlank(header)) {
            URI uri = UriBuilder.fromUri(getRootUrl()).path(url).build();
            String msg = String.format("Not Found: %s", uri);
            throw ApiException.notFound(msg);
        }

        return Arrays.asList(header.split(","));
    }
}
