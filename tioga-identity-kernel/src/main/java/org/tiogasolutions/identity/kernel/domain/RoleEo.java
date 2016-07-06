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
    private final PolicyEo policy;

    private RoleEo(PolicyEo policy,
                   @JsonProperty("id") String id,
                   @JsonProperty("roleName") String roleName) {

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

    public String toString() {
        return getId();
    }

    public static RoleEo createRole(PolicyEo policy, String roleName) {
        String id = policy.getIdPath() + ":" + roleName;
        return new RoleEo(policy, id, roleName);
    }
}
