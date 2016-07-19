package org.tiogasolutions.identity.client;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.identity.client.core.PubItem;
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
        int pos = url.indexOf("?");
        if (pos >= 0) {
            url = url.substring(0, pos);
        }

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

    protected ApiException buildException(HttpStatusCode statusCode, String content) {

        int length = (content == null) ? -1 : content.length();
        String msg = String.format("Unexpected response: %s %s", statusCode.getCode(), statusCode.getReason());

        try {
            PubItem item = translator.fromJson(PubItem.class, content);
            msg = item.get_status().getMessage();

        } catch (Exception ignored) {/*ignored*/}

        String[] traits = {
                String.format("length:%s", length),
                String.format("content:%s", content)
        };

        return ApiException.fromCode(statusCode, msg, traits);
    }
}
