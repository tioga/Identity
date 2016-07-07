package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;

public class AssignedRoleEo {

    private final String policyId;
    private final String realmId;
    private final String roleId;

    private AssignedRoleEo(@JsonProperty("policyId") String policyId,
                           @JsonProperty("realmId") String realmId,
                           @JsonProperty("roleId") String roleId) {
        this.policyId = policyId;
        this.realmId = realmId;
        this.roleId = roleId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getRealmId() {
        return realmId;
    }

    public String getRoleId() {
        return roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssignedRoleEo that = (AssignedRoleEo) o;

        if (!policyId.equals(that.policyId)) return false;
        if (!realmId.equals(that.realmId)) return false;
        return roleId.equals(that.roleId);

    }

    @Override
    public int hashCode() {
        int result = policyId.hashCode();
        result = 31 * result + realmId.hashCode();
        result = 31 * result + roleId.hashCode();
        return result;
    }

    public static AssignedRoleEo create(RealmEo realm, RoleEo role) {

        if (EqualsUtils.objectsNotEqual(realm.getPolicy().getId(), role.getPolicy().getId())) {
            throw ApiException.internalServerError("Incompatible policies");
        }

        return new AssignedRoleEo(
                realm.getPolicy().getId(),
                realm.getId(),
                role.getId());
    }

    public boolean permitsRealm(RealmEo realm) {
        return EqualsUtils.objectsEqual(realmId, realm.getId());
    }
}
