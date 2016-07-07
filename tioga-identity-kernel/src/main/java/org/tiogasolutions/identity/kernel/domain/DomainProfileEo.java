package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.identity.client.domain.DomainStatus;

import java.util.*;

import static java.util.Collections.emptyList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;

public class DomainProfileEo {

    public static final String INTERNAL_DOMAIN = "internal";

    private String domainName;
    private String revision;
    private DomainStatus status;
    private String dbName;

    private final Map<String,String> authorizationTokens = new HashMap<>();

    @JsonBackReference
    private final List<PolicyEo> policies = new ArrayList<>();

    public DomainProfileEo(@JsonProperty("domainName") String domainName,
                           @JsonProperty("revision") String revision,
                           @JsonProperty("status") DomainStatus status,
                           @JsonProperty("authorizationTokens") Map<String,String> authorizationTokens,
                           @JsonProperty("dbName") String dbName,
                           @JsonProperty("policies") List<PolicyEo> policies) {

        this.domainName = ExceptionUtils.assertNotZeroLength(domainName, "name").toLowerCase();
        this.revision = revision;
        this.status = status;
        this.dbName = dbName;

        if (authorizationTokens != null) this.authorizationTokens.putAll(authorizationTokens);
        if (policies != null) this.policies.addAll(policies);
    }

    public final String getRevision() {
        return revision;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDbName() {
        return dbName;
    }

    public DomainStatus getStatus() {
        return status;
    }

    public List<PolicyEo> getPolicies() {
        return policies;
    }

    public Map<String, String> getAuthorizationTokens() {
        return Collections.unmodifiableMap(authorizationTokens);
    }

    public void generateAccessToken(String name) {
        this.authorizationTokens.put(name, UUID.randomUUID().toString());
    }

    public void setAccessToken(String name, String token) {
        this.authorizationTokens.put(name, token);
    }

    public PolicyEo findPolicyById(String id) {
        for (PolicyEo policy : policies) {
            if (objectsEqual(id, policy.getId())) {
                return policy;
            }
        }
        throw ApiException.notFound("The specified policy was not found.");
    }

    public RealmEo findRealmByName(String realmName) {
        for (PolicyEo policy : policies) {
            for (RealmEo realm : policy.getRealms()) {
                if (objectsEqual(realmName, realm.getRealmName())) {
                    return realm;
                }
            }
        }
        throw ApiException.notFound("The specified realm was not found.");
    }

    public RealmEo findRealmById(String id) {
        for (PolicyEo policy : policies) {
            for (RealmEo realm : policy.getRealms()) {
                if (objectsEqual(id, realm.getId())) {
                    return realm;
                }
            }
        }
        throw ApiException.notFound("The specified realm was not found.");
    }

    public RoleEo findRoleById(String id) {
        for (PolicyEo policy : policies) {
            for (RoleEo role : policy.getRoles()) {
                if (objectsEqual(id, role.getId())) {
                    return role;
                }
            }
        }
        throw ApiException.notFound("The specified role was not found.");
    }

    public PermissionEo findPermissionById(String id) {
        for (PolicyEo policy : policies) {
            for (PermissionEo permission : policy.getPermissions()) {
                if (objectsEqual(id, permission.getId())) {
                    return permission;
                }
            }
        }
        throw ApiException.notFound("The specified role was not found.");
    }

    public PolicyEo addPolicy(String policyName) {
        PolicyEo policy = PolicyEo.createPolicy(this, policyName);
        policies.add(policy);
        return policy;
    }

    public static DomainProfileEo create(String domainName) {
        return new DomainProfileEo(
                domainName,
                "0",
                DomainStatus.ACTIVE,
                Collections.emptyMap(),
                "identity-"+domainName,
                emptyList());
    }

    public String toString() {
        return getDomainName();
    }
}



