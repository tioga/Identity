package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RoleEo {

    private final String roleName;
    private final List<PermissionEo> permissions = new ArrayList<>();

    public RoleEo(@JsonProperty("roleName") String roleName,
                  @JsonProperty("permissions") List<PermissionEo> permissions) {

        this.roleName = roleName;
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getRoleName() {
        return roleName;
    }

    public List<PermissionEo> getPermissions() {
        return unmodifiableList(permissions);
    }
}
