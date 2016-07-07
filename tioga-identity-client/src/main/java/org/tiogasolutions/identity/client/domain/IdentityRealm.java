package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityRealm {

    private final String id;
    private final String realmName;

    public IdentityRealm(@JsonProperty("id") String id,
                         @JsonProperty("realmName") String realmName) {

        this.id = id;
        this.realmName = realmName;
    }

    public String getId() {
        return id;
    }

    public String getRealmName() {
        return realmName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityRealm that = (IdentityRealm) o;

        if (!id.equals(that.id)) return false;
        return realmName.equals(that.realmName);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + realmName.hashCode();
        return result;
    }
}
