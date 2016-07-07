package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.domain.*;
import org.tiogasolutions.identity.kernel.domain.*;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.stream.Collectors;

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






    public IdentityToken toToken(HttpStatusCode statusCode, DomainProfileEo domainProfile, String tokenName) {
        PubLinks links = new PubLinks();
        links.add("self", uriToken(tokenName));

        links.add("me", uriMe());

        // We cannot check the security context because we may have just authenticated in
        // which case the SC thinks we are anonymous even though this *IS* the admin domainProfile.
        if (INTERNAL_DOMAIN.equals(domainProfile.getDomainName())) {
            links.add("admin",   uriAdmin());
            links.add("admin-domains", uriDomains(null, null, null));
            links.add("admin-domains-links", uriDomains(singletonList("links"), null, null));
        }

        links.add("policies",       uriPolicies(null));
        links.add("policies-links", uriPolicies(singletonList("links")));
        links.add("policies-items", uriPolicies(singletonList("items")));

        links.add("identities",       uriIdentities(null, null, null, null));
        links.add("identities-links", uriIdentities(singletonList("links"), null, null, null));
        links.add("identities-items", uriIdentities(singletonList("items"), null, null, null));

        return new IdentityToken(
                toStatus(statusCode),
                links,
                tokenName,
                domainProfile.getDomainName(),
                domainProfile.getAuthorizationTokens().get(tokenName)
        );
    }






    public IdentityDomain toDomainProfile(SecurityContext sc, HttpStatusCode statusCode, DomainProfileEo domainProfile) {

        PubLinks links = new PubLinks();
        links.add("self", uriMe());

        if (sc != null && sc.isUserInRole($ADMIN)) {
            links.add("admin",   uriAdmin());
            links.add("admin-domains", uriDomains(null, null, null));
        }

        links.add("policies",       uriPolicies(null));
        links.add("policies-links", uriPolicies(singletonList("links")));
        links.add("policies-items", uriPolicies(singletonList("items")));

        links.add("identities",       uriIdentities(null, null, null, null));
        links.add("identities-links", uriIdentities(singletonList("links"), null, null, null));
        links.add("identities-items", uriIdentities(singletonList("items"), null, null, null));

        List<IdentityPolicy> pubPolicies = new ArrayList<>();
        for (PolicyEo policy : domainProfile.getPolicies()) {
            IdentityPolicy identityPolicy = toPolicy(null, policy);
            pubPolicies.add(identityPolicy);
        }

        return new IdentityDomain(
                toStatus(statusCode),
                links,
                domainProfile.getDomainName(),
                domainProfile.getRevision(),
                domainProfile.getStatus(),
                domainProfile.getAuthorizationTokens(),
                domainProfile.getDbName(),
                pubPolicies);
    }

    public IdentityDomains toDomains(HttpStatusCode statusCode, List<DomainProfileEo> domainProfiles, List<String> includes, Object offset, Object limit) {
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

        return new IdentityDomains(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                999999999,
                includes.contains("links") ? linksList : null);
    }

    public IdentityPolicy toPolicy(HttpStatusCode statusCode, PolicyEo policy) {

        PubLinks links = new PubLinks();
        links.add("self", uriPolicyById(policy));

        List<IdentityRole> roles = new ArrayList<>();
        for (RoleEo role : policy.getRoles()) {
            IdentityRole identityRole = toRole(role);
            roles.add(identityRole);
        }

        List<IdentityRealm> realms = new ArrayList<>();
        for (RealmEo realm : policy.getRealms()) {
            IdentityRealm identityRealm = toRealm(realm);
            realms.add(identityRealm);
        }

        Set<String> permissions = policy.getPermissions().stream()
                .map(PermissionEo::getPermissionName)
                .collect(Collectors.toSet());

        return new IdentityPolicy(
                toStatus(statusCode),
                links,
                policy.getId(),
                policy.getPolicyName(),
                policy.getDomainProfile().getDomainName(),
                roles,
                realms,
                permissions
        );
    }

    public IdentityRealm toRealm(RealmEo realm) {

        return new IdentityRealm(
                realm.getId(),
                realm.getRealmName());
    }

    public IdentityRole toRole(RoleEo role) {

        Set<String> permissions = role.getPermissions().stream()
                .map(PermissionEo::getPermissionName)
                .collect(Collectors.toSet());

        return new IdentityRole(
                role.getRoleName(),
                permissions);
    }

    public IdentityPolicies toPolicies(HttpStatusCode statusCode, DomainProfileEo domainProfile, List<String> includes) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();
        List<PolicyEo> policies = domainProfile.getPolicies();

        links.add("self",       uriPolicies(includes));
        links.add("self-items", uriPolicies(singletonList("items")));
        links.add("self-links", uriPolicies(singletonList("links")));

        List<IdentityPolicy> itemsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (PolicyEo policy : policies) {
            IdentityPolicy identityPolicy = toPolicy(null, policy);
            itemsList.add(identityPolicy);
            linksList.add(identityPolicy.get_links().get("self").clone(identityPolicy.getPolicyName()));
        }

        return new IdentityPolicies(
                toStatus(statusCode),
                links,
                itemsList.size(),
                includes.contains("items") ? itemsList : null,
                includes.contains("links") ? linksList : null);
    }





    public Identity toIdentity(HttpStatusCode statusCode, DomainProfileEo domain, IdentityEo identity) {

        PubLinks links = new PubLinks();
        links.add("self", uriUserById(identity));

        Set<IdentityGrant> grants = new HashSet<>();
        Set<IdentityRole> roles = new HashSet<>();

        for (AssignedRoleEo assignedRole : identity.getAssignedRoles()) {
            PolicyEo policy = domain.findPolicyById(assignedRole.getPolicyId());
            RealmEo realm = policy.findRealmById(assignedRole.getRealmId());
            RoleEo role = policy.findRoleById(assignedRole.getRoleId());

            List<String> permissions = role.getPermissions().stream()
                    .map(PermissionEo::getPermissionName)
                    .collect(Collectors.toList());

            grants.add(new IdentityGrant(realm.getRealmName(), permissions));
            roles.add(new IdentityRole(role.getRoleName(), permissions));
        }

        return new Identity(
                toStatus(statusCode),
                links,
                identity.getId(),
                identity.getRevision(),
                identity.getUsername(),
                identity.getPassword(),
                identity.getDomainName(),
                grants,
                roles);
    }

    public Identities toIdentities(HttpStatusCode statusCode, DomainProfileEo domain, List<IdentityEo> identities, List<String> includes, String username, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriIdentities(includes, username, offset, limit));
        links.add("self-items", uriIdentities(singletonList("items"), username, offset, limit));
        links.add("self-links", uriIdentities(singletonList("links"), username, offset, limit));

        links.add("identity",   uriUserById(null));
        links.add("api",    uriApi());

        if (identities.size() > 0) {
            IdentityEo first = identities.get(0);
            links.add("first-identity", uriUserById(first));
        }

        links.add("first", uriIdentities(null, username, 0, limit));
        links.add("prev",  uriIdentities(null, username, 0, limit));
        links.add("next",  uriIdentities(null, username, 0, limit));
        links.add("last",  uriIdentities(null, username, 0, limit));

        List<Identity> usersList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (IdentityEo user : identities) {
            Identity pubUser = toIdentity(null, domain, user);
            usersList.add(pubUser);

            linksList.add(pubUser.get_links().get("self").clone(pubUser.getUsername()));
        }

        return new Identities(
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

    public String uriToken(String tokenName) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($tokens)
                .path(tokenName)
                .toTemplate();
    }

    public String uriMe() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .toTemplate();
    }

    public String uriPolicyById(PolicyEo policy) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($policies)
                .path(policy.getId())
                .toTemplate();
    }

//    public String uriRealmById(RealmEo realm) {
//        return uriInfo.getBaseUriBuilder()
//                .path($api_v1)
//                .path($realms)
//                .path(realm.getId())
//                .toTemplate();
//    }

//    public String uriRoleById(RoleEo role) {
//        return uriInfo.getBaseUriBuilder()
//                .path($api_v1)
//                .path($roles)
//                .path(role.getId())
//                .toTemplate();
//    }

    private String uriPolicies(List<String> includes) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($policies);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriIdentities(List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, Identities.DEFAULT_LIMIT, "limit");
        if (username == null) username = "";

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($identities)
                .queryParam("username", username)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUserById(IdentityEo user) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($identities)
                .path(user == null ? "{id}" : user.getId())
                .toTemplate();
    }

    public static int toInt(Object value, Object defaultValue, String paramName) {
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
