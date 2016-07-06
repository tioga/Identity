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
    private final PolicyEo policy;

    private RealmEo(PolicyEo policy,
                    @JsonProperty("id") String id,
                    @JsonProperty("realmName") String realmName) {

        this.policy = policy;

        this.id = id;
        this.realmName = realmName;
    }

    public String getId() {
        return id;
    }

    public String getRealmName() {
        return realmName;
    }

    public PolicyEo getPolicy() {
        return policy;
    }

    public String toString() {
        return getId();
    }

    public static RealmEo createRealm(PolicyEo policy, String realmName) {
        String id = policy.getIdPath() + ":" + realmName;
        return new RealmEo(policy, id, realmName);
    }
}
