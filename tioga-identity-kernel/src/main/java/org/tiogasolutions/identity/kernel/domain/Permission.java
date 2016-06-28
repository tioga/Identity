package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Permission {

    private final String permissionName;

    @JsonManagedReference
    private final RoleEo role;

    private Permission(RoleEo role,
                       @JsonProperty("name") String permissionName) {

        this.role = role;

        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public RoleEo getRole() {
        return role;
    }

    public static Permission create(RoleEo role, String permissionName) {
        return new Permission(role, permissionName);
    }
}
