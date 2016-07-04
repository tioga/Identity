package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

public class UserEo {

    private String id;
    private String revision;
    private String username;
    private String password;
    private Set<String> assignedRoles = new TreeSet<>();

    private String domainName;

    @JsonCreator
    private UserEo(@JsonProperty("id") String id,
                   @JsonProperty("revision") String revision,
                   @JsonProperty("domainName") String domainName,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("assignedRoles") List<String> assignedRoles) {

        this.domainName = domainName;

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;
        if (assignedRoles != null) this.assignedRoles.addAll(assignedRoles);
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

    public Set<String> getAssignedRoles() {
        return unmodifiableSet(assignedRoles);
    }

    public String getDomainName() {
        return domainName;
    }

    public UserEo assign(RoleEo...roles) {
        for (RoleEo role : roles) {
            assignedRoles.add(role.toAssignedRole());
        }
        return this;
    }

    public static UserEo create(DomainProfileEo domainProfile, String username, String password) {

        String id = domainProfile.getDomainName() + ":" + username;

        return new UserEo(
            id,
            null,
            domainProfile.getDomainName(),
            username,
            password,
            emptyList()
        );
    }
}
