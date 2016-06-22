package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class TenantProfileEo {

    private final String profileId;
    private final String revision;
    private final String tenantName;
    private final TenantStatus tenantStatus;
    private final String apiToken;
    private final String tenantDbName;

    private final List<UserEo> users = new ArrayList<>();
    private final List<RealmEo> realms = new ArrayList<>();

    public TenantProfileEo(@JsonProperty("profileId") String profileId,
                           @JsonProperty("revision") String revision,
                           @JsonProperty("tenantName") String tenantName,
                           @JsonProperty("tenantStatus") TenantStatus tenantStatus,
                           @JsonProperty("apiToken") String apiToken,
                           @JsonProperty("tenantDbName") String tenantDbName,
                           @JsonProperty("realms") List<RealmEo> realms,
                           @JsonProperty("users") List<UserEo> users) {

        this.profileId = profileId;
        this.revision = revision;
        this.tenantName = tenantName;
        this.tenantStatus = tenantStatus;
        this.apiToken = apiToken;
        this.tenantDbName = tenantDbName;

        if (users != null) this.users.addAll(users);
        if (realms != null) this.realms.addAll(realms);
    }

    public String getProfileId() {
        return profileId;
    }

    public final String getRevision() {
        return revision;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getTenantDbName() {
        return tenantDbName;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
    }

    public List<RealmEo> getRealms() {
        return unmodifiableList(realms);
    }

    public List<UserEo> getUsers() {
        return unmodifiableList(users);
    }

    public UserEo findUserByName(String username) {
        for (UserEo user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public UserEo findUserById(String id) {
        for (UserEo user : users) {
            if (id.equals(user.getId())) {
                return user;
            }
        }
        return null;
    }

    public RoleEo findRole(String roleName) {
        for (RealmEo realm : realms) {
            for (RoleEo role : realm.getRoles()) {
                if (roleName.equals(role.getRoleName())) {
                    return role;
                }
            }
        }
        return null;
    }

    public RealmEo findRealm(String realmName) {
        for (RealmEo realm : realms) {
            if (realm.equals(realm.getRealmName())) {
                return realm;
            }
        }
        return null;
    }

    public RealmEo createRealm(String realmName) {
        RealmEo realm = new RealmEo(realmName, emptyList());
        realms.add(realm);
        return realm;
    }

    public UserEo createUser(String username, String password, RoleEo...roles) {
        UserEo user = new UserEo(username, password, roles);
        users.add(user);
        return user;
    }
}
