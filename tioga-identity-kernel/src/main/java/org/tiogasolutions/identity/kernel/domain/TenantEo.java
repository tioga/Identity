package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;

public class TenantEo {

    private final String id;
    private final String revision;
    private final String name;
    private final TenantStatus status;
    private final String apiToken;
    private final String dbName;

    private final List<UserEo> users = new ArrayList<>();
    private final List<RealmEo> realms = new ArrayList<>();

    public TenantEo(@JsonProperty("id") String id,
                    @JsonProperty("revision") String revision,
                    @JsonProperty("name") String name,
                    @JsonProperty("status") TenantStatus status,
                    @JsonProperty("apiToken") String apiToken,
                    @JsonProperty("dbName") String dbName,
                    @JsonProperty("realms") List<RealmEo> realms,
                    @JsonProperty("users") List<UserEo> users) {

        this.id = id;
        this.revision = revision;
        this.name = name;
        this.status = status;
        this.apiToken = apiToken;
        this.dbName = dbName;

        if (users != null) this.users.addAll(users);
        if (realms != null) this.realms.addAll(realms);
    }

    public String getId() {
        return id;
    }

    public final String getRevision() {
        return revision;
    }

    public String getName() {
        return name;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getDbName() {
        return dbName;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public List<RealmEo> getRealms() {
        return unmodifiableList(realms);
    }

    public List<UserEo> getUsers() {
        return unmodifiableList(users);
    }


    public List<UserEo> getUsers(String username) {
        if (StringUtils.isBlank(username)) {
            return unmodifiableList(users);

        } else {
            List<UserEo> usersList = users.stream().filter(user -> objectsEqual(username, user.getUsername())).collect(toList());
            return unmodifiableList(usersList);
        }
    }

    public UserEo findUserByName(String username) {
        for (UserEo user : users) {
            if (objectsEqual(username, user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public UserEo findUserById(String id) {
        for (UserEo user : users) {
            if (objectsEqual(id,user.getId())) {
                return user;
            }
        }
        return null;
    }

    public RoleEo findRole(String roleName) {
        for (RealmEo realm : realms) {
            for (RoleEo role : realm.getRoles()) {
                if (objectsEqual(roleName, role.getRoleName())) {
                    return role;
                }
            }
        }
        return null;
    }

    public RealmEo findRealm(String realmName) {
        for (RealmEo realm : realms) {
            if (objectsEqual(realm, realm.getRealmName())) {
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
