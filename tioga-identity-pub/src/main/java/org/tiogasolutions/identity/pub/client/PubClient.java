package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.core.DomainStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubClient extends PubItem {

    private final String clientName;
    private final String revision;
    private final DomainStatus status;
    private final Map<String,String> authorizationTokens = new HashMap<>();
    private final String password;
    private final String dbName;

    private final List<PubSystem> systems = new ArrayList<>();

    public PubClient(@JsonProperty("_status")PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("clientName") String clientName,
                     @JsonProperty("revision") String revision,
                     @JsonProperty("status") DomainStatus status,
                     @JsonProperty("authorizationToken") Map<String,String> authorizationTokens,
                     @JsonProperty("password") String password,
                     @JsonProperty("dbName") String dbName,
                     @JsonProperty("systems") List<PubSystem> systems) {

        super(_status, _links);

        this.revision = revision;
        this.clientName = clientName;
        this.status = status;
        this.password = password;
        this.dbName = dbName;

        if (authorizationTokens != null) this.authorizationTokens.putAll(authorizationTokens);
        if (systems != null) this.systems.addAll(systems);
    }

    public Map<String, String> getAuthorizationTokens() {
        return authorizationTokens;
    }

    public String getPassword() {
        return password;
    }

    public String getRevision() {
        return revision;
    }

    public String getClientName() {
        return clientName;
    }

    public DomainStatus getStatus() {
        return status;
    }

    public String getDbName() {
        return dbName;
    }

    public List<PubSystem> getSystems() {
        return systems;
    }
}
