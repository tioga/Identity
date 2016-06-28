package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RealmEo {

    private final String id;
    private final String realmName;

    @JsonManagedReference
    private final List<RoleEo> roles = new ArrayList<>();

    @JsonManagedReference
    private final SystemEo system;

    private RealmEo(SystemEo system,
                    @JsonProperty("id") String id,
                    @JsonProperty("realmName") String realmName,
                    @JsonProperty("roles") List<RoleEo> roles) {

        this.system = system;

        this.id = id;
        this.realmName = realmName;
        if (roles != null) this.roles.addAll(roles);
    }

    public String getId() {
        return id;
    }

    public String getRealmName() {
        return realmName;
    }

    public SystemEo getSystem() {
        return system;
    }

    public List<RoleEo> getRoles() {
        return unmodifiableList(roles);
    }

    public RoleEo createRole(String roleName) {
        RoleEo role = RoleEo.create(this, roleName);
        roles.add(role);
        return role;
    }

    public static RealmEo createRealm(SystemEo system, String realmName) {

        String id = system.getIdPath() + ":" + realmName;

        return new RealmEo(
                system,
                id,
                realmName,
                Collections.emptyList()
        );
    }

    public String getIdPath() {
        return getSystem().getClient().getClientName() + ":" + getSystem().getSystemName() + ":" + getRealmName();
    }

    public String toString() {
        return getIdPath();
    }
}
