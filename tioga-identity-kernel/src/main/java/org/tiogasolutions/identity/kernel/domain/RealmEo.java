package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class RealmEo {

    private final String realmName;
    private final List<RoleEo> roles = new ArrayList<>();

    public RealmEo(@JsonProperty("realmName") String realmName,
                   @JsonProperty("roles") List<RoleEo> roles) {

        this.realmName = realmName;
        if (roles != null) this.roles.addAll(roles);
    }

    public String getRealmName() {
        return realmName;
    }

    public List<RoleEo> getRoles() {
        return unmodifiableList(roles);
    }

    public RoleEo createRole(String roleName) {
        RoleEo role = new RoleEo(roleName, emptyList());
        roles.add(role);
        return role;
    }
}
