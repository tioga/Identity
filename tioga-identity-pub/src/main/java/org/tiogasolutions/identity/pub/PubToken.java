package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

public class PubToken extends PubItem {

    public static final String DEFAULT = "default";
    public static final String ADMIN = "admin";

    private final String domainName;
    private final String tokenName;
    private final String authorizationToken;

    public PubToken(@JsonProperty("_status")PubStatus _status,
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
