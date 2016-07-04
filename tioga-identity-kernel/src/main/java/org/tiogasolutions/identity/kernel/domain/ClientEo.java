package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.identity.pub.core.DomainStatus;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;

public class ClientEo {

    private String clientName;
    private String revision;
    private DomainStatus status;
    private String password;
    private String dbName;

    private Map<String,String> authorizationTokens = new HashMap<>();

    @JsonBackReference
    private final List<SystemEo> systems = new ArrayList<>();

    public ClientEo(@JsonProperty("clientName") String clientName,
                    @JsonProperty("revision") String revision,
                    @JsonProperty("status") DomainStatus status,
                    @JsonProperty("authorizationTokens") Map<String,String> authorizationTokens,
                    @JsonProperty("password") String password,
                    @JsonProperty("dbName") String dbName,
                    @JsonProperty("systems") List<SystemEo> systems) {

        this.clientName = ExceptionUtils.assertNotZeroLength(clientName, "name").toLowerCase();
        this.revision = revision;
        this.status = status;
        this.authorizationTokens = authorizationTokens;
        this.password = password;
        this.dbName = dbName;

        if (systems != null) this.systems.addAll(systems);
    }

    public String getPassword() {
        return password;
    }

    public final String getRevision() {
        return revision;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDbName() {
        return dbName;
    }

    public DomainStatus getStatus() {
        return status;
    }

    public List<SystemEo> getSystems() {
        return systems;
    }

    public Map<String, String> getAuthorizationTokens() {
        return Collections.unmodifiableMap(authorizationTokens);
    }

    public void generateAccessToken(String name) {
        this.authorizationTokens.put(name, UUID.randomUUID().toString());
    }

    public SystemEo findSystemById(String id) {
        for (SystemEo system : systems) {
            if (objectsEqual(id, system.getId())) {
                return system;
            }
        }
        throw ApiException.notFound("The specified system was not found.");
    }

    public RealmEo findRealmById(String id) {
        for (SystemEo system : systems) {
            for (RealmEo realm : system.getRealms()) {
                if (objectsEqual(id, realm.getId())) {
                    return realm;
                }
            }
        }
        throw ApiException.notFound("The specified realm was not found.");
    }

    public RoleEo findRoleById(String id) {
        for (SystemEo system : systems) {
            for (RealmEo realm : system.getRealms()) {
                for (RoleEo role : realm.getRoles())
                    if (objectsEqual(id, role.getId())) {
                        return role;
                    }
            }
        }
        throw ApiException.notFound("The specified role was not found.");
    }

    public SystemEo addSystem(String systemName) {
        SystemEo system = SystemEo.createSystem(this, systemName);
        systems.add(system);
        return system;
    }

    public static ClientEo create(String name, String password) {
        return new ClientEo(
                name,
                "0",
                DomainStatus.ACTIVE,
                BeanUtils.toMap("default:"+UUID.randomUUID().toString()),
                password,
                "identity-"+name,
                emptyList());
    }

    public String toString() {
        return getClientName();
    }
}



