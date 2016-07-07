package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableSet;

public class IdentityRole {

    private final String roleName;
    private final Set<String> permissions = new TreeSet<>();

    public IdentityRole(@JsonProperty("roleName") String roleName,
                        @JsonProperty("permissions") Collection<String> permissions) {

        this.roleName = roleName;

        if (permissions != null) this.permissions.addAll(permissions);
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<String> getPermissions() {
        return unmodifiableSet(permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityRole that = (IdentityRole) o;

        if (!roleName.equals(that.roleName)) return false;
        return permissions.equals(that.permissions);

    }

    @Override
    public int hashCode() {
        int result = roleName.hashCode();
        result = 31 * result + permissions.hashCode();
        return result;
    }
}
