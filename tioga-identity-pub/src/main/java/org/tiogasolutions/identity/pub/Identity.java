package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.*;

import static java.util.Collections.*;

public class Identity extends PubItem {

    private final String id;
    private final String revision;
    private final String username;
    private final String password;
    private final String domainName;

    private final Map<String,IdentityGrant> grants = new HashMap<>();
    private final Map<String,IdentityRole> roles = new HashMap<>();

    @JsonCreator
    public Identity(@JsonProperty("_status") PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("id") String id,
                    @JsonProperty("revision") String revision,
                    @JsonProperty("username") String username,
                    @JsonProperty("password") String password,
                    @JsonProperty("domainName") String domainName,
                    @JsonProperty("grants") Map<String, IdentityGrant> grants,
                    @JsonProperty("roles") Map<String, IdentityRole> roles) {

        super(_status, _links);

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;
        this.domainName = domainName;

        if (grants != null) this.grants.putAll(grants);
        if (roles != null) this.roles.putAll(roles);
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

    public Map<String, IdentityGrant> getGrants() {
        return unmodifiableMap(grants);
    }

    public Map<String, IdentityRole> getRoles() {
        return unmodifiableMap(roles);
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
