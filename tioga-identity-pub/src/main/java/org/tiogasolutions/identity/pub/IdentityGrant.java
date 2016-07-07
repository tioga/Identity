package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.*;

public class IdentityGrant extends PubItem {

    public final String id;
    private final String realmName;
    private final Set<String> permissions = new HashSet<>();

    public IdentityGrant(@JsonProperty("_status") PubStatus _status,
                         @JsonProperty("_links") PubLinks _links,
                         @JsonProperty("id") String id,
                         @JsonProperty("realmName") String realmName,
                         @JsonProperty("permissions") List<String> permissions) {

        super(_status, _links);

        this.id = id;
        this.realmName = realmName;
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getRealmName() {
        return realmName;
    }

    public Set<String> getPermissions() {
        return unmodifiableSet(permissions);
    }
}
