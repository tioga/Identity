package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class PubRole extends PubItem {

    public final String id;
    private final String roleName;
    private final String realmName;
    private final String systemName;
    private final String clientName;

    private final List<PubPermission> permissions = new ArrayList<>();

    public PubRole(@JsonProperty("_status") PubStatus _status,
                   @JsonProperty("_links") PubLinks _links,
                   @JsonProperty("id") String id,
                   @JsonProperty("roleName") String roleName,
                   @JsonProperty("clientName") String clientName,
                   @JsonProperty("systemName") String systemName,
                   @JsonProperty("realmName") String realmName,
                   @JsonProperty("permissions") List<PubPermission> permissions) {

        super(_status, _links);

        this.id = id;
        this.roleName = roleName;
        this.realmName = realmName;
        this.systemName = systemName;
        this.clientName = clientName;

        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getClientName() {
        return clientName;
    }

    public List<PubPermission> getPermissions() {
        return unmodifiableList(permissions);
    }
}
