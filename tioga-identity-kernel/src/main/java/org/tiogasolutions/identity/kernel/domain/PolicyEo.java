package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class PolicyEo {

    private final String id;
    private final String policyName;

    @JsonManagedReference
    private final DomainProfileEo domainProfile;

    @JsonBackReference
    private final List<RoleEo> roles = new ArrayList<>();

    @JsonBackReference
    private final List<RealmEo> realms = new ArrayList<>();

    @JsonBackReference
    private final List<PermissionEo> permissions = new ArrayList<>();

    private PolicyEo(DomainProfileEo domainProfile,
                     @JsonProperty("id") String id,
                     @JsonProperty("policyName") String policyName,
                     @JsonProperty("roles") List<RoleEo> roles,
                     @JsonProperty("realms") List<RealmEo> realms,
                     @JsonProperty("permissions") List<PermissionEo> permissions) {

        this.domainProfile = domainProfile;

        this.id = id;
        this.policyName = policyName;

        if (roles != null) this.roles.addAll(roles);
        if (realms != null) this.realms.addAll(realms);
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public DomainProfileEo getDomainProfile() {
        return domainProfile;
    }

    public List<RealmEo> getRealms() {
        return unmodifiableList(realms);
    }

    public RealmEo addRealm(String realmName) {
        RealmEo realm = RealmEo.createRealm(this, realmName);
        realms.add(realm);
        return realm;
    }

    public List<RoleEo> getRoles() {
        return unmodifiableList(roles);
    }

    public RoleEo addRole(String roleName) {
        RoleEo role = RoleEo.createRole(this, roleName);
        roles.add(role);
        return role;
    }

    public List<PermissionEo> getPermissions() {
        return unmodifiableList(permissions);
    }

    public List<PermissionEo> getPermissions(List<String> permissionIds) {
        return unmodifiableList(permissions.stream()
                .filter(p -> permissionIds.contains(p.getId()))
                .collect(Collectors.toList()));
    }

    public PermissionEo addPermission(String permissionName) {
        PermissionEo permission = PermissionEo.createPermission(this, permissionName);
        permissions.add(permission);
        return permission;
    }

    public String getIdPath() {
        return getDomainProfile().getDomainName() + ":" + getPolicyName();
    }

    public String toString() {
        return getIdPath();
    }

    public static PolicyEo createPolicy(DomainProfileEo domainProfile, String policyName) {

        String id = domainProfile.getDomainName() + ":" + policyName;

        return new PolicyEo(
                domainProfile,
                id,
                policyName,
                emptyList(),    // roles
                emptyList(),    // realms
                emptyList());   // permissions
    }
}
