package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

public class PubToken extends PubItem {

    public static final String DEFAULT = "default";
    public static final String ADMIN = "admin";

    private final String clientName;
    private final String tokenName;
    private final String authorizationToken;

    public PubToken(@JsonProperty("_status")PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("tokenName") String tokenName,
                    @JsonProperty("clientName") String clientName,
                    @JsonProperty("authorizationToken") String authorizationToken) {

        super(_status, _links);

        this.clientName = clientName;
        this.tokenName = tokenName;
        this.authorizationToken = authorizationToken;
    }

    public String getClientName() {
        return clientName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
