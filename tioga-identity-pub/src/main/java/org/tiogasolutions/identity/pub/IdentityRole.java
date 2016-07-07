package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.*;

public class IdentityRole extends PubItem {

    public final String id;
    private final String roleName;
    private final String policyName;

    private final Set<String> permissions = new TreeSet<>();

    public IdentityRole(@JsonProperty("_status") PubStatus _status,
                        @JsonProperty("_links") PubLinks _links,
                        @JsonProperty("id") String id,
                        @JsonProperty("roleName") String roleName,
                        @JsonProperty("policyName") String policyName,
                        @JsonProperty("permissions") Set<String> permissions) {

        super(_status, _links);

        this.id = id;
        this.roleName = roleName;
        this.policyName = policyName;

        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public Set<String> getPermissions() {
        return unmodifiableSet(permissions);
    }
}
