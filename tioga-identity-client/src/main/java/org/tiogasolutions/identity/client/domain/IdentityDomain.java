package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityDomain extends PubItem {

    private final String domainName;
    private final String revision;
    private final DomainStatus status;
    private final Map<String,String> authorizationTokens = new HashMap<>();
    private final String dbName;

    private final List<IdentityPolicy> policies = new ArrayList<>();

    public IdentityDomain(@JsonProperty("_status")PubStatus _status,
                          @JsonProperty("_links") PubLinks _links,
                          @JsonProperty("domainName") String domainName,
                          @JsonProperty("revision") String revision,
                          @JsonProperty("status") DomainStatus status,
                          @JsonProperty("authorizationToken") Map<String,String> authorizationTokens,
                          @JsonProperty("dbName") String dbName,
                          @JsonProperty("policies") List<IdentityPolicy> policies) {

        super(_status, _links);

        this.revision = revision;
        this.domainName = domainName;
        this.status = status;
        this.dbName = dbName;

        if (authorizationTokens != null) this.authorizationTokens.putAll(authorizationTokens);
        if (policies != null) this.policies.addAll(policies);
    }

    public Map<String, String> getAuthorizationTokens() {
        return authorizationTokens;
    }

    public String getRevision() {
        return revision;
    }

    public String getDomainName() {
        return domainName;
    }

    public DomainStatus getStatus() {
        return status;
    }

    public String getDbName() {
        return dbName;
    }

    public List<IdentityPolicy> getPolicies() {
        return policies;
    }
}
