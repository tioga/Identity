package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class PolicyEo {

    private final String id;
    private final String policyName;

    @JsonBackReference
    private final List<RealmEo> realms = new ArrayList<>();

    @JsonManagedReference
    private final DomainProfileEo domainProfile;

    private PolicyEo(DomainProfileEo domainProfile,
                     @JsonProperty("id") String id,
                     @JsonProperty("policyName") String policyName,
                     @JsonProperty("realms") List<RealmEo> realms) {

        this.domainProfile = domainProfile;

        this.id = id;
        this.policyName = policyName;
        if (realms != null) this.realms.addAll(realms);
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
        return realms;
    }

    public RealmEo addRealm(String realmName) {
        RealmEo realm = RealmEo.createRealm(this, realmName);
        realms.add(realm);
        return realm;
    }

    public static PolicyEo createPolicy(DomainProfileEo domainProfile, String policyName) {

        String id = domainProfile.getDomainName() + ":" + policyName;

        return new PolicyEo(
                domainProfile,
                id,
                policyName,
                emptyList());
    }

    public String getIdPath() {
        return getDomainProfile().getDomainName() + ":" + getPolicyName();
    }

    public String toString() {
        return getIdPath();
    }
}
