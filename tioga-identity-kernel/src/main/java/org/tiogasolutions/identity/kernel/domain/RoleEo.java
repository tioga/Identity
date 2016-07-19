package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class RoleEo {

    public final String id;
    private final String roleName;

    @JsonManagedReference
    private final PolicyEo policy;

    private final List<String> permissionIds = new ArrayList<>();

    private RoleEo(PolicyEo policy,
                   @JsonProperty("id") String id,
                   @JsonProperty("roleName") String roleName,
                   @JsonProperty("permissionIds") List<String> permissionIds) {

        this.policy = policy;

        this.id = id;
        this.roleName = roleName;
    }

    public String getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public PolicyEo getPolicy() {
        return policy;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    @JsonIgnore
    public List<PermissionEo> getPermissions() {
        return policy.getPermissions(permissionIds);
    }

    public String toString() {
        return getId();
    }

    public static RoleEo createRole(PolicyEo policy, String roleName) {
        String id = policy.getIdPath() + ":" + roleName;
        return new RoleEo(policy, id, roleName, emptyList());
    }

    public void assign(PermissionEo permission) {
        this.permissionIds.add(permission.getId());
    }
}
