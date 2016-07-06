package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PermissionEo {

    private final String id;
    private final String permissionName;

    @JsonManagedReference
    private final PolicyEo policy;

    private PermissionEo(PolicyEo policy,
                         @JsonProperty("id") String id,
                         @JsonProperty("permissionName") String permissionName) {

        this.id = id;

        this.policy = policy;
        this.permissionName = permissionName;
    }

    public String getId() {
        return id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public PolicyEo getPolicy() {
        return policy;
    }

    public static PermissionEo createPermission(PolicyEo policy, String permissionName) {
        String id = policy.getIdPath() + ":" + permissionName;
        return new PermissionEo(policy, id, permissionName);
    }
}
