package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class UserEo {

    private String id;
    private String revision;
    private String username;
    private String password;
    private List<String> assignedRoles = new ArrayList<>();

    @JsonCreator
    public UserEo(@JsonProperty("id") String id,
                  @JsonProperty("revision") String revision,
                  @JsonProperty("username") String username,
                  @JsonProperty("password") String password,
                  @JsonProperty("assignedRoles") List<String> assignedRoles) {

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;
        if (assignedRoles != null) this.assignedRoles.addAll(assignedRoles);
    }

    public UserEo(String username, String password, RoleEo...roles) {
        if (roles == null) roles = new RoleEo[0];

        this.id = TimeUuid.randomUUID().toString();
        this.revision = null;
        this.username = username;
        this.password = password;

        for (RoleEo role : roles) {
            assignedRoles.add(role.getName());
        }
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

    public List<String> getAssignedRoles() {
        return unmodifiableList(assignedRoles);
    }

    public UserEo assign(RoleEo...roles) {
        for (RoleEo role : roles) {
            assignedRoles.add(role.getName());
        }
        return this;
    }
}
