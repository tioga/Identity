package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PubPermission {

    private final String permissionName;
    private final String roleName;
    private final String realmName;
    private final String policyName;
    private final String domainName;

    public PubPermission(@JsonProperty("permissionName") String permissionName,
                         @JsonProperty("domainName") String domainName,
                         @JsonProperty("policyName") String policyName,
                         @JsonProperty("realmName") String realmName,
                         @JsonProperty("roleName") String roleName) {

        this.permissionName = permissionName;
        this.roleName = roleName;
        this.realmName = realmName;
        this.policyName = policyName;
        this.domainName = domainName;
    }

    public String getPermissionName() {
        return permissionName;
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
}
