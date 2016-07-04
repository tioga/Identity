package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class PubRole extends PubItem {

    public final String id;
    private final String roleName;
    private final String realmName;
    private final String policyName;
    private final String domainName;

    private final List<PubPermission> permissions = new ArrayList<>();

    public PubRole(@JsonProperty("_status") PubStatus _status,
                   @JsonProperty("_links") PubLinks _links,
                   @JsonProperty("id") String id,
                   @JsonProperty("roleName") String roleName,
                   @JsonProperty("domainName") String domainName,
                   @JsonProperty("policyName") String policyName,
                   @JsonProperty("realmName") String realmName,
                   @JsonProperty("permissions") List<PubPermission> permissions) {

        super(_status, _links);

        this.id = id;
        this.roleName = roleName;
        this.realmName = realmName;
        this.policyName = policyName;
        this.domainName = domainName;

        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<PubPermission> getPermissions() {
        return unmodifiableList(permissions);
    }
}
