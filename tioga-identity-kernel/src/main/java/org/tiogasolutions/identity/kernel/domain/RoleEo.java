package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;
import static java.util.Collections.unmodifiableList;

public class RoleEo {

    public final String id;
    private final String roleName;

    @JsonManagedReference
    private final List<Permission> permissions = new ArrayList<>();

    @JsonManagedReference
    private final RealmEo realm;

    private RoleEo(RealmEo realm,
                   @JsonProperty("id") String id,
                   @JsonProperty("roleName") String roleName,
                   @JsonProperty("permissions") List<Permission> permissions) {

        this.realm = realm;

        this.id = id;
        this.roleName = roleName;
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public RealmEo getRealm() {
        return realm;
    }

    public List<Permission> getPermissions() {
        return unmodifiableList(permissions);
    }

    public String toAssignedRole() {
        return String.format("%s:%s:%s",
                realm.getSystem().getSystemName(),
                realm.getRealmName(),
                roleName);
    }

    public static RoleEo create(RealmEo realm, String roleName) {

        String id = realm.getIdPath() + ":" + roleName;

        return new RoleEo(
                realm,
                id,
                roleName,
                emptyList()
        );
    }

    public void addPermission(String permissionName) {
        Permission permission = Permission.create(this, permissionName);
        this.permissions.add(permission);
    }

    public String getIdPath() {
        return getRealm().getSystem().getClient().getClientName() + ":" + getRealm().getSystem().getSystemName() + ":" + getRealm().getRealmName() + ":" + getRoleName();
    }

    public String toString() {
        return getIdPath();
    }
}
