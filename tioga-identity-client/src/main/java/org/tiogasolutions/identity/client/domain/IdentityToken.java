package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

public class IdentityToken extends PubItem {

    private final String domainName;
    private final String tokenName;
    private final String authorizationToken;

    public IdentityToken(@JsonProperty("_status")PubStatus _status,
                         @JsonProperty("_links") PubLinks _links,
                         @JsonProperty("tokenName") String tokenName,
                         @JsonProperty("domainName") String domainName,
                         @JsonProperty("authorizationToken") String authorizationToken) {

        super(_status, _links);

        this.domainName = domainName;
        this.tokenName = tokenName;
        this.authorizationToken = authorizationToken;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
