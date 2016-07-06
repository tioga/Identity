package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.kernel.domain.*;
import org.tiogasolutions.identity.pub.*;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLink;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;
import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;
import static org.tiogasolutions.identity.kernel.domain.DomainProfileEo.INTERNAL_DOMAIN;

public class PubUtils {

    private final UriInfo uriInfo;

    public PubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public PubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public Response.ResponseBuilder toResponse(PubItem pubItem) {
        return Response.status(pubItem.get_status().getCode()).entity(pubItem);
    }






    public PubToken toToken(HttpStatusCode statusCode, DomainProfileEo domainProfile, String tokenName) {
        PubLinks links = new PubLinks();
        links.add("self", uriToken());

        links.add("client", uriClient());

        // We cannot check the security context because we may have just authenticated in
        // which case the SC thinks we are anonymous even though this *IS* the admin domainProfile.
        if (INTERNAL_DOMAIN.equals(domainProfile.getDomainName())) {
            links.add("admin",   uriAdmin());
            links.add("admin-domains", uriDomains(null, null, null));
            links.add("admin-domains-links", uriDomains(singletonList("links"), null, null));
        }

        links.add("policies",       uriPolicies(null, null, null));
        links.add("policies-links", uriPolicies(singletonList("links"), null, null));
        links.add("policies-items", uriPolicies(singletonList("items"), null, null));

        links.add("users",       uriUsers(null, null, null, null));
        links.add("users-links", uriUsers(singletonList("links"), null, null, null));
        links.add("users-items", uriUsers(singletonList("items"), null, null, null));

        return new PubToken(
                toStatus(statusCode),
                links,
                tokenName,
                domainProfile.getDomainName(),
                domainProfile.getAuthorizationTokens().get(PubToken.DEFAULT)
        );
    }






    public PubDomain toDomainProfile(SecurityContext sc, HttpStatusCode statusCode, DomainProfileEo domainProfile) {

        PubLinks links = new PubLinks();
        links.add("self", uriClient());

        if (sc != null && sc.isUserInRole($ADMIN)) {
            links.add("admin",   uriAdmin());
            links.add("admin-domains", uriDomains(null, null, null));
        }

        links.add("policies",       uriPolicies(null, null, null));
        links.add("policies-links", uriPolicies(singletonList("links"), null, null));
        links.add("policies-items", uriPolicies(singletonList("items"), null, null));

        links.add("users",       uriUsers(null, null, null, null));
        links.add("users-links", uriUsers(singletonList("links"), null, null, null));
        links.add("users-items", uriUsers(singletonList("items"), null, null, null));

        List<PubPolicy> pubPolicies = new ArrayList<>();
        for (PolicyEo policy : domainProfile.getPolicies()) {
            PubPolicy pubPolicy = toPolicy(null, policy);
            pubPolicies.add(pubPolicy);
        }

        return new PubDomain(
                toStatus(statusCode),
                links,
                domainProfile.getDomainName(),
                domainProfile.getRevision(),
                domainProfile.getStatus(),
                domainProfile.getAuthorizationTokens(),
                domainProfile.getPassword(),
                domainProfile.getDbName(),
                pubPolicies);
    }

    public PubDomains toDomains(HttpStatusCode statusCode, List<DomainProfileEo> domainProfiles, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriDomains(includes, offset, limit));
        links.add("self-links", uriDomains(singletonList("links"), offset, limit));

        links.add("first", uriDomains(includes, offset, limit));
        links.add("prev",  uriDomains(includes, offset, limit));
        links.add("next",  uriDomains(includes, offset, limit));
        links.add("last",  uriDomains(includes, offset, limit));

        List<PubLink> linksList = new ArrayList<>();
        for (DomainProfileEo domainProfile : domainProfiles) {
            linksList.add(new PubLink(domainProfile.getDomainName(), uriAdminDomain(domainProfile)));
            linksList.add(new PubLink("impersonate-" + domainProfile.getDomainName(), uriImpersonate(domainProfile)));
        }

        return new PubDomains(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                999999999,
                includes.contains("links") ? linksList : null);
    }

    public PubPolicy toPolicy(HttpStatusCode statusCode, PolicyEo policy) {

        PubLinks links = new PubLinks();
        links.add("self", uriPolicyById(policy));

        List<PubRealm> realms = new ArrayList<>();
        for (RealmEo realm : policy.getRealms()) {
            PubRealm pubRealm = toRealm(null, realm);
            realms.add(pubRealm);
        }

        return new PubPolicy(
                toStatus(statusCode),
                links,
                policy.getId(),
                policy.getPolicyName(),
                policy.getDomainProfile().getDomainName(),
                realms
        );
    }

    public PubRealm toRealm(HttpStatusCode statusCode, RealmEo realm) {

        PubLinks links = new PubLinks();
        links.add("self", uriRealmById(realm));

        List<PubRole> roles = new ArrayList<>();
        for (RoleEo role : realm.getRoles()) {
            PubRole pubRole = toRole(null, role);
            roles.add(pubRole);
        }

        return new PubRealm(
                toStatus(statusCode),
                links,
                realm.getId(),
                realm.getRealmName(),
                realm.getPolicy().getDomainProfile().getDomainName(),
                realm.getPolicy().getPolicyName(),
                roles);
    }

    public PubRole toRole(HttpStatusCode statusCode, RoleEo role) {

        PubLinks links = new PubLinks();
        links.add("self", uriRoleById(role));

        List<PubPermission> permissions = new ArrayList<>();
        for (PermissionEo permission : role.getPermissions()) {
            PubPermission pubPermission = toPermission(null, permission);
            permissions.add(pubPermission);
        }

        return new PubRole(
                toStatus(statusCode),
                links,
                role.getId(),
                role.getRoleName(),
                role.getRealm().getPolicy().getDomainProfile().getDomainName(),
                role.getRealm().getPolicy().getPolicyName(),
                role.getRealm().getRealmName(),
                permissions);
    }

    private PubPermission toPermission(HttpStatusCode statusCode, PermissionEo permission) {

        return new PubPermission(
                permission.getPermissionName(),
                permission.getRole().getRealm().getPolicy().getDomainProfile().getDomainName(),
                permission.getRole().getRealm().getPolicy().getPolicyName(),
                permission.getRole().getRealm().getRealmName(),
                permission.getRole().getRoleName());
    }

    public PubPolicies toPolicies(HttpStatusCode statusCode, DomainProfileEo domainProfile, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();
        List<PolicyEo> policies = domainProfile.getPolicies();

        links.add("self",       uriPolicies(includes, offset, limit));
        links.add("self-items", uriPolicies(singletonList("items"), offset, limit));
        links.add("self-links", uriPolicies(singletonList("links"), offset, limit));

        links.add("first", uriPolicies(null, 0, limit));
        links.add("prev",  uriPolicies(null, 0, limit));
        links.add("next",  uriPolicies(null, 0, limit));
        links.add("last",  uriPolicies(null, 0, limit));

        List<PubPolicy> itemsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (PolicyEo policy : policies) {
            PubPolicy pubPolicy = toPolicy(null, policy);
            itemsList.add(pubPolicy);
            linksList.add(pubPolicy.get_links().get("self").clone(pubPolicy.getPolicyName()));
        }

        return new PubPolicies(
                toStatus(statusCode),
                links,
                itemsList.size(),
                itemsList.size(),
                0,
                999999999,
                includes.contains("items") ? itemsList : null,
                includes.contains("links") ? linksList : null);
    }





    public PubUser toUser(HttpStatusCode statusCode, UserEo user) {

        PubLinks links = new PubLinks();
        links.add("self", uriUserById(user));

        return new PubUser(
                toStatus(statusCode),
                links,
                user.getId(),
                user.getRevision(),
                user.getUsername(),
                user.getPassword(),
                user.getDomainName(),
                user.getAssignedRoles()
        );
    }

    public PubUsers toUsers(HttpStatusCode statusCode, List<UserEo> users, List<String> includes, String username, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriUsers(includes, username, offset, limit));
        links.add("self-items", uriUsers(singletonList("items"), username, offset, limit));
        links.add("self-links", uriUsers(singletonList("links"), username, offset, limit));

        links.add("user",   uriUserById(null));
        links.add("api",    uriApi());

        if (users.size() > 0) {
            UserEo first = users.get(0);
            links.add("first-user", uriUserById(first));
        }

        links.add("first", uriUsers(null, username, 0, limit));
        links.add("prev",  uriUsers(null, username, 0, limit));
        links.add("next",  uriUsers(null, username, 0, limit));
        links.add("last",  uriUsers(null, username, 0, limit));

        List<PubUser> usersList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserEo user = users.get(i);
            PubUser pubUser = toUser(null, user);
            usersList.add(pubUser);

            linksList.add(pubUser.get_links().get("self").clone(pubUser.getUsername()));
        }

        return new PubUsers(
                toStatus(statusCode),
                links,
                usersList.size(),
                usersList.size(),
                0,
                999999999,
                includes.contains("items") ? usersList : null,
                includes.contains("links") ? linksList : null);
    }

    private PubStatus toStatus(HttpStatusCode statusCode) {
        return statusCode == null ? null : new PubStatus(statusCode);
    }

    public String uriRoot() {
        return uriInfo.getBaseUriBuilder().toTemplate();
    }

    public String uriApi() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .toTemplate();
    }

    public String uriAuthenticate() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($authenticate)
                .toTemplate();
    }

    public String uriAdmin() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .toTemplate();
    }

    private String uriAdminDomain(DomainProfileEo domainProfile) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains)
                .path(domainProfile.getDomainName())
                .toTemplate();
    }

    private String uriImpersonate(DomainProfileEo domainProfile) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains)
                .path(domainProfile.getDomainName())
                .path($impersonate)
                .toTemplate();
    }

    public String uriDomains(List<String> includes, Object offset, Object limit) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriToken() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($token)
                .toTemplate();
    }

    public String uriClient() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .toTemplate();
    }

    public String uriPolicyById(PolicyEo policy) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($policies)
                .path(policy.getId())
                .toTemplate();
    }

    public String uriRealmById(RealmEo realm) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($realms)
                .path(realm.getId())
                .toTemplate();
    }

    public String uriRoleById(RoleEo role) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($roles)
                .path(role.getId())
                .toTemplate();
    }

    private String uriPolicies(List<String> includes, Object offsetObj, Object limitObj) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, PubUsers.DEFAULT_LIMIT, "limit");

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($policies)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUsers(List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, PubUsers.DEFAULT_LIMIT, "limit");
        if (username == null) username = "";

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($users)
                .queryParam("username", username)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUserById(UserEo user) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($users)
                .path(user == null ? "{id}" : user.getId())
                .toTemplate();
    }

    private int toInt(Object value, Object defaultValue, String paramName) {
        if (value == null && defaultValue == null) {
            throw ApiException.badRequest(String.format("The parameter %s must be specified.", paramName));

        } else if (value == null) {
            value = defaultValue;
        }

        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            throw ApiException.badRequest(String.format("The parameter %s must be an integral value.", paramName));
        }
    }
}
