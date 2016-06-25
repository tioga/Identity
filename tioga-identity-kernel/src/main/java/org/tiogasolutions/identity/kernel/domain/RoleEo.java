package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RoleEo {

    private final String name;
    private final List<PermissionEo> permissions = new ArrayList<>();

    public RoleEo(@JsonProperty("roleName") String name,
                  @JsonProperty("permissions") List<PermissionEo> permissions) {

        this.name = name;
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getName() {
        return name;
    }

    public List<PermissionEo> getPermissions() {
        return unmodifiableList(permissions);
    }
}
