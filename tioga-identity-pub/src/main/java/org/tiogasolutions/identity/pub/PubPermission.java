package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PubPermission {

    private final String permissionName;
    private final String roleName;
    private final String realmName;
    private final String systemName;
    private final String domainName;

    public PubPermission(@JsonProperty("permissionName") String permissionName,
                         @JsonProperty("domainName") String domainName,
                         @JsonProperty("systemName") String systemName,
                         @JsonProperty("realmName") String realmName,
                         @JsonProperty("roleName") String roleName) {

        this.permissionName = permissionName;
        this.roleName = roleName;
        this.realmName = realmName;
        this.systemName = systemName;
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

    public String getSystemName() {
        return systemName;
    }

    public String getDomainName() {
        return domainName;
    }
}
