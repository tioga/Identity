package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

public class PubPermission {

    private final String permissionName;
    private final String roleName;
    private final String realmName;
    private final String systemName;
    private final String clientName;

    public PubPermission(@JsonProperty("permissionName") String permissionName,
                         @JsonProperty("clientName") String clientName,
                         @JsonProperty("systemName") String systemName,
                         @JsonProperty("realmName") String realmName,
                         @JsonProperty("roleName") String roleName) {

        this.permissionName = permissionName;
        this.roleName = roleName;
        this.realmName = realmName;
        this.systemName = systemName;
        this.clientName = clientName;
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

    public String getClientName() {
        return clientName;
    }
}
