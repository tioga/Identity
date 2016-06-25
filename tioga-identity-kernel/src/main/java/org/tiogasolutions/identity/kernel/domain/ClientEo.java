package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;

public class ClientEo {

    private String name;
    private String revision;
    private TenantStatus status;
    private String authorizationToken;
    private String password;
    private String dbName;

    private final List<UserEo> users = new ArrayList<>();
    private final List<SystemEo> systems = new ArrayList<>();

    public ClientEo(@JsonProperty("name") String name,
                    @JsonProperty("revision") String revision,
                    @JsonProperty("status") TenantStatus status,
                    @JsonProperty("authorizationToken") String authorizationToken,
                    @JsonProperty("password") String password,
                    @JsonProperty("dbName") String dbName,
                    @JsonProperty("users") List<UserEo> users,
                    @JsonProperty("systems") List<SystemEo> systems) {

        this.name = ExceptionUtils.assertNotZeroLength(name, "name").toLowerCase();
        this.revision = revision;
        this.status = status;
        this.authorizationToken = authorizationToken;
        this.password = password;
        this.dbName = dbName;

        if (users != null) this.users.addAll(users);
        if (systems != null) this.systems.addAll(systems);
    }

    public String getPassword() {
        return password;
    }

    public final String getRevision() {
        return revision;
    }

    public String getName() {
        return name;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getDbName() {
        return dbName;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public List<SystemEo> getSystems() {
        return systems;
    }

    public List<UserEo> getUsers() {
        return unmodifiableList(users);
    }

    public void generateAccessToken() {
        this.authorizationToken = TimeUuid.randomUUID().toString();
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

    public SystemEo createSystem(String systemName) {
        SystemEo system = SystemEo.createSystem(this, systemName);
        systems.add(system);
        return system;
    }

    public UserEo createUser(String username, String password) {
        UserEo user = new UserEo(username, password);
        users.add(user);
        return user;
    }

    public static ClientEo create(String name, String password) {
        return new ClientEo(
                name,
                "0",
                TenantStatus.ACTIVE,
                TimeUuid.randomUUID().toString(),
                password,
                "identity-"+name,
                emptyList(),
                emptyList());
    }
}



