package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PubPermission {

    private final String permissionName;
    private final String policyName;

    public PubPermission(@JsonProperty("permissionName") String permissionName,
                         @JsonProperty("policyName") String policyName) {

        this.permissionName = permissionName;
        this.policyName = policyName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public String getPolicyName() {
        return policyName;
    }
}
