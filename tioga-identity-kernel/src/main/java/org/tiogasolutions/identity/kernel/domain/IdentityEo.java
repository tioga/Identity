package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

import static java.util.Collections.*;

public class IdentityEo {

    private String id;
    private String revision;
    private String username;
    private String password;

    private String domainName;

    private Set<AssignedRoleEo> assignedRoleEos = new HashSet<>();

    @JsonCreator
    private IdentityEo(@JsonProperty("id") String id,
                       @JsonProperty("revision") String revision,
                       @JsonProperty("domainName") String domainName,
                       @JsonProperty("username") String username,
                       @JsonProperty("password") String password,
                       @JsonProperty("assignedRoles") List<AssignedRoleEo> assignedRoleEos) {

        this.domainName = domainName;

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;

        if (assignedRoleEos != null) this.assignedRoleEos.addAll(assignedRoleEos);
    }

    public String getRevision() {
        return revision;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomainName() {
        return domainName;
    }

    public Set<AssignedRoleEo> getAssignedRoles() {
        return unmodifiableSet(assignedRoleEos);
    }

    public IdentityEo assign(RealmEo realm, RoleEo...roles) {
        for (RoleEo role : roles) {
            AssignedRoleEo ias = AssignedRoleEo.create(realm, role);
            assignedRoleEos.add(ias);
        }
        return this;
    }

    public static IdentityEo create(DomainProfileEo domainProfile, String username, String password) {

        String id = domainProfile.getDomainName() + ":" + username;

        return new IdentityEo(
            id,
            null,
            domainProfile.getDomainName(),
            username,
            password,
            emptyList());
    }

    public boolean permits(RealmEo realm) {
        for (AssignedRoleEo assignedRoleEo : assignedRoleEos) {
            if (assignedRoleEo.permitsRealm(realm)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return getId();
    }
}
