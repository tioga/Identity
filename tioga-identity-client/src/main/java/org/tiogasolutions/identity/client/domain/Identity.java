package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

import java.util.*;

import static java.util.Collections.*;

public class Identity extends PubItem {

    private final String id;
    private final String revision;
    private final String username;
    private final String password;
    private final String domainName;

    private final Set<IdentityGrant> grants = new HashSet<>();
    private final Set<IdentityRole> roles = new HashSet<>();

    @JsonCreator
    public Identity(@JsonProperty("_status") PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("id") String id,
                    @JsonProperty("revision") String revision,
                    @JsonProperty("username") String username,
                    @JsonProperty("password") String password,
                    @JsonProperty("domainName") String domainName,
                    @JsonProperty("grants") Set<IdentityGrant> grants,
                    @JsonProperty("roles") Set<IdentityRole> roles) {

        super(_status, _links);

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;
        this.domainName = domainName;

        if (grants != null) this.grants.addAll(grants);
        if (roles != null) this.roles.addAll(roles);
    }

    public String getDomainName() {
        return domainName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<IdentityGrant> getGrants() {
        return unmodifiableSet(grants);
    }

    public Set<IdentityRole> getRoles() {
        return unmodifiableSet(roles);
    }

    public String getId() {
        return id;
    }

    public String getRevision() {
        return revision;
    }

    public String toString() {
        return getId();
    }
}
