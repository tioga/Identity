package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.*;

public class PubUser extends PubItem {

    private final String id;
    private final String revision;
    private final String username;
    private final String password;
    private final String clientName;
    private final Set<String> assignedRoles = new TreeSet<>();

    @JsonCreator
    public PubUser(@JsonProperty("_status") PubStatus _status,
                   @JsonProperty("_links") PubLinks _links,
                   @JsonProperty("id") String id,
                   @JsonProperty("revision") String revision,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("clientName") String clientName,
                   @JsonProperty("assignedRoles") Set<String> assignedRoles) {

        super(_status, _links);

        this.id = id;
        this.revision = revision;
        this.username = username;
        this.password = password;
        this.clientName = clientName;

        if (assignedRoles != null) this.assignedRoles.addAll(assignedRoles);
    }

    public String getClientName() {
        return clientName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getAssignedRoles() {
        return assignedRoles;
    }

    public String getId() {
        return id;
    }

    public String getRevision() {
        return revision;
    }
}
