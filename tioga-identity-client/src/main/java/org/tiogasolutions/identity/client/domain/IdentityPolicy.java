package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

public class IdentityPolicy extends PubItem {

    private final String id;
    private final String policyName;
    private final String domainName;

    private final List<IdentityRealm> realms = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();
    private final List<IdentityRole> roles = new ArrayList<>();

    @JsonCreator
    public IdentityPolicy(@JsonProperty("_status") PubStatus _status,
                          @JsonProperty("_links") PubLinks _links,
                          @JsonProperty("id") String id,
                          @JsonProperty("policyName") String policyName,
                          @JsonProperty("domainName") String domainName,
                          @JsonProperty("roles") List<IdentityRole> roles,
                          @JsonProperty("realms") List<IdentityRealm> realms,
                          @JsonProperty("permissions") Set<String> permissions) {

        super(_status, _links);

        this.id = id;
        this.policyName = policyName;
        this.domainName = domainName;

        if (roles != null) this.roles.addAll(roles);
        if (realms != null) this.realms.addAll(realms);
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<IdentityRole> getRoles() {
        return unmodifiableList(roles);
    }

    public List<IdentityRealm> getRealms() {
        return unmodifiableList(realms);
    }

    public Set<String> getPermissions() {
        return unmodifiableSet(permissions);
    }
}
