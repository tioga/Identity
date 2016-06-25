package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.core.TenantStatus;

public class PubClient extends PubItem {

    private final String name;
    private final String revision;
    private final TenantStatus status;
    private final String authorizationToken;
    private final String password;
    private final String dbName;

    public PubClient(@JsonProperty("_status")PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("name") String name,
                     @JsonProperty("revision") String revision,
                     @JsonProperty("status") TenantStatus status,
                     @JsonProperty("authorizationToken") String authorizationToken,
                     @JsonProperty("password") String password,
                     @JsonProperty("dbName") String dbName) {

        super(_status, _links);

        this.revision = revision;
        this.name = name;
        this.status = status;
        this.authorizationToken = authorizationToken;
        this.password = password;
        this.dbName = dbName;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getPassword() {
        return password;
    }

    public String getRevision() {
        return revision;
    }

    public String getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public String getDbName() {
        return dbName;
    }
}
