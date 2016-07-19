package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableSet;

public class IdentityGrant {

    private final String realmName;
    private final Set<String> permissions = new TreeSet<>();

    public IdentityGrant(@JsonProperty("realmName") String realmName,
                         @JsonProperty("permissions") Collection<String> permissions) {

        this.realmName = realmName;
        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getRealmName() {
        return realmName;
    }

    public Set<String> getPermissions() {
        return unmodifiableSet(permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityGrant that = (IdentityGrant) o;

        if (!realmName.equals(that.realmName)) return false;
        return permissions.equals(that.permissions);

    }

    @Override
    public int hashCode() {
        int result = realmName.hashCode();
        result = 31 * result + permissions.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return realmName + " " + permissions;
    }
}
