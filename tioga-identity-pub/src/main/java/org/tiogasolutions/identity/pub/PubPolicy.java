package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class PubPolicy extends PubItem {

    private final String id;
    private final String policyName;
    private final String domainName;

    private final List<PubRole> roles = new ArrayList<>();
    private final List<PubRealm> realms = new ArrayList<>();
    private final List<PubPermission> permissions = new ArrayList<>();

    @JsonCreator
    public PubPolicy(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("id") String id,
                     @JsonProperty("policyName") String policyName,
                     @JsonProperty("domainName") String domainName,
                     @JsonProperty("roles") List<PubRole> roles,
                     @JsonProperty("realms") List<PubRealm> realms,
                     @JsonProperty("permissions") List<PubPermission> permissions) {

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

    public List<PubRole> getRoles() {
        return unmodifiableList(roles);
    }

    public List<PubRealm> getRealms() {
        return unmodifiableList(realms);
    }

    public List<PubPermission> getPermissions() {
        return unmodifiableList(permissions);
    }
}
