package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class UserEo {

    private final String id;
    private final String revision;
    private final String username;
    private final String password;
    private final List<String> assignedRoles = new ArrayList<>();

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
            assignedRoles.add(role.getRoleName());
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

    public List<String> getAssignedRoles() {
        return unmodifiableList(assignedRoles);
    }
}
