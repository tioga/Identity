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
import java.net.URI;
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
        Response.ResponseBuilder builder = Response.status(pubItem.get_status().getCode()).entity(pubItem);

        for (PubLink link : pubItem.get_links().values()) {
            builder.link(link.getHref(), link.getRel());
        }

        return builder;
    }





    public IdentityToken toToken(HttpStatusCode statusCode, DomainProfileEo domainProfile, String tokenName) {
        PubLinks links = PubLinks.self(lnkToken(tokenName));
        links.add(lnkAuthenticatedTokens());

        links.add(lnkMe());

        // We cannot check the security context because we may have just authenticated in
        // which case the SC thinks we are anonymous even though this *IS* the admin domainProfile.
        if (INTERNAL_DOMAIN.equals(domainProfile.getDomainName())) {
            links.add(lnkAdmin());
            links.addAll(lnkDomains(null, null, null));
        }

        links.addAll(lnkPolicies());
        links.addAll(lnkIdentities(null, null, null, null));

        return new IdentityToken(
                toStatus(statusCode),
                links,
                tokenName,
                domainProfile.getDomainName(),
                domainProfile.getAuthorizationTokens().get(tokenName)
        );
    }

    public IdentityTokens toTokens(HttpStatusCode statusCode, DomainProfileEo domainProfile, List<String> includes) {
        if (includes == null) includes = emptyList();

        PubLinks links = PubLinks.self(lnkAuthenticatedTokens());
        links.add(lnkMe());

        Map<String, String> tokens = domainProfile.getAuthorizationTokens();
        List<PubLink> linksList = new ArrayList<>();
        List<IdentityToken> itemsList = new ArrayList<>();

        for (String tokenName : tokens.keySet()) {
            itemsList.add(toToken(null, domainProfile, tokenName));
            linksList.add(lnkAdminDomain(domainProfile));
        }

        return new IdentityTokens(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                Integer.MAX_VALUE,
                includes.contains("items") ? itemsList : null,
                includes.contains("links") ? linksList : null);
    }







    public IdentityDomain toDomainProfile(SecurityContext sc, HttpStatusCode statusCode, DomainProfileEo domainProfile) {

        PubLinks links = PubLinks.self(lnkMe());

        if (sc != null && sc.isUserInRole($ADMIN)) {
            links.add(lnkAdmin());
            links.addAll(lnkDomains(null, null, null));
        }

        links.addAll(lnkPolicies());
        links.addAll(lnkIdentities(null, null, null, null));

        return new IdentityDomain(
                toStatus(statusCode),
                links,
                domainProfile.getDomainName(),
                domainProfile.getRevision(),
                domainProfile.getStatus(),
                domainProfile.getAuthorizationTokens(),
                domainProfile.getDbName());
    }

    public IdentityDomains toDomains(HttpStatusCode statusCode, List<DomainProfileEo> domainProfiles, List<String> includes, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        PubLinks links = PubLinks.self(lnkDomains(includes, offsetObj, limitObj));

        links.addAll(lnkDomainsFPNL(includes, offsetObj, limitObj));

        List<PubLink> linksList = new ArrayList<>();
        for (DomainProfileEo domainProfile : domainProfiles) {
            linksList.add(lnkAdminDomain(domainProfile).clone(domainProfile.getDomainName()));
            linksList.add(lnkImpersonate(domainProfile).clone("impersonate-"+domainProfile.getDomainName()));
        }

        return new IdentityDomains(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                Integer.MAX_VALUE,
                includes.contains("links") ? linksList : null);
    }

    public IdentityPolicy toPolicy(HttpStatusCode statusCode, PolicyEo policy) {

        PubLinks links = PubLinks.self(lnkPolicyById(policy));

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

        PubLinks links = PubLinks.self(lnkPolicies());

        List<IdentityPolicy> itemsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        List<PolicyEo> policies = domainProfile.getPolicies();

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

        PubLinks links = PubLinks.self(lnkIdentityById(identity));
        links.add(lnkIdentityByUsername(identity));
        links.addAll(lnkPolicies());

        Map<String,List<String>>  grantsMap = new HashMap<>();
        Set<IdentityRole> roles = new HashSet<>();

        for (AssignedRoleEo assignedRole : identity.getAssignedRoles()) {
            PolicyEo policy = domain.findPolicyById(assignedRole.getPolicyId());
            RealmEo realm = policy.findRealmById(assignedRole.getRealmId());
            RoleEo role = policy.findRoleById(assignedRole.getRoleId());

            List<String> permissions = role.getPermissions().stream()
                    .map(PermissionEo::getPermissionName)
                    .collect(Collectors.toList());

            roles.add(new IdentityRole(role.getRoleName(), permissions));

            if (grantsMap.containsKey(realm.getRealmName()) == false) {
                grantsMap.put(realm.getRealmName(), new ArrayList<>());
            }
            grantsMap.get(realm.getRealmName()).addAll(permissions);
        }

        Set<IdentityGrant> grants = new HashSet<>();
        for (Map.Entry<String,List<String>> entry : grantsMap.entrySet()) {
            IdentityGrant grant = new IdentityGrant(entry.getKey(), entry.getValue());
            grants.add(grant);
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

    public Identities toIdentities(HttpStatusCode statusCode, DomainProfileEo domain, List<IdentityEo> identities, List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        PubLinks links = PubLinks.self(lnkIdentities(includes, username, offsetObj, limitObj));

        links.addAll(lnkIdentitiesFPNL(includes, username, offsetObj, limitObj));

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

    public PubLink lnkAnonymousInfo() {
        String href = uriInfo.getBaseUriBuilder().path($api_v1).path($anonymous).path($info).toTemplate();
        return PubLink.create("status", href, "GET: Fetch the current system status.");
    }

    public PubLink lnkApiV1() {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .toTemplate();

        return PubLink.create("api-v1", href);
    }

    public PubLink lnkAdmin() {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .toTemplate();

        return PubLink.create("admin", href, "GET: Administer all profiles for this system.");
    }

    private PubLink lnkAdminDomain(DomainProfileEo domainProfile) {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains)
                .path(domainProfile.getDomainName())
                .toTemplate();

        return PubLink.create("admin-domain", href, "GET: Fetch list of all domains: ~includes[links,items]");
    }

    private PubLink lnkImpersonate(DomainProfileEo domainProfile) {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains)
                .path(domainProfile.getDomainName())
                .path($impersonate)
                .toTemplate();

        return PubLink.create("impersonate", href, "GET: Impersonate the specified domain.");
    }

    public List<PubLink> lnkDomains(List<String> includes, Object offsetObj, Object limitObj) {
        List<PubLink> list = new ArrayList<>();
        list.add(createDomainsLnk(includes, offsetObj, limitObj));
        list.add(createDomainsLnk(singletonList("links"), offsetObj, limitObj));
        list.add(createDomainsLnk(singletonList("items"), offsetObj, limitObj));
        return list;
    }

    public PubLink createDomainsLnk(List<String> includes, Object offsetObj, Object limitObj) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($domains);

        addOffsetAndLimit(builder, offsetObj, limitObj);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        String href = builder.toTemplate();
        return PubLink.create("admin-domains", href);
    }

    public List<PubLink     > lnkDomainsFPNL(List<String> includes, Object offsetObj, Object limitObj) {
        List<PubLink> list = new ArrayList<>();
        list.add(createDomainsLnk(includes, offsetObj, limitObj).toFirst());
        list.add(createDomainsLnk(includes, offsetObj, limitObj).toPrev());
        list.add(createDomainsLnk(includes, offsetObj, limitObj).toNext());
        list.add(createDomainsLnk(includes, offsetObj, limitObj).toLast());
        return list;
    }

    public PubLink lnkAuthenticatedTokens() {
        String href = uriInfo.getBaseUriBuilder().path($api_v1).path($me).path($tokens).toTemplate();
        return PubLink.create("tokens", href, "POST: Generate a new token for the specified user: *domainName, *username, *password.","GET: Fetch all tokens.");
    }

    public PubLink lnkAnonymousTokens() {
        String href = uriInfo.getBaseUriBuilder().path($api_v1).path($anonymous).path($tokens).toTemplate();
        return PubLink.create("tokens", href, "POST: Generate a new token for the specified user: *domainName, *username, *password.");
    }

    public PubLink lnkToken(String tokenName) {
        String href = uriInfo.getBaseUriBuilder().path($api_v1).path($me).path($tokens).path(tokenName).toTemplate();
        return PubLink.create("token", href, "GET: Fetch the specified token.");
    }

    public PubLink lnkMe() {

        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .toTemplate();

        return PubLink.create("me", href, "GET: Fetch the profile for the currently authenticated user.");
    }

    public PubLink lnkPolicyById(PolicyEo policy) {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($policies)
                .path(policy.getId())
                .toTemplate();

        return PubLink.create("policy", href, "GET: Fetch the specific policy.");
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


    private List<PubLink> lnkPolicies() {
        List<PubLink> list = new ArrayList<>();
        list.add(createPoliciesLnk(null));
        list.add(createPoliciesLnk(singletonList("links")).toLinks());
        list.add(createPoliciesLnk(singletonList("items")).toItems());
        return list;
    }

    private PubLink createPoliciesLnk(List<String> includes) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($policies);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        String href = builder.toTemplate();
        return PubLink.create("policies", href);
    }

    public List<PubLink> lnkIdentities(List<String> includes, String username, Object offsetObj, Object limitObj) {
        List<PubLink> list = new ArrayList<>();
        list.add(createIdentitiesLnk(includes, username, offsetObj, limitObj));
        list.add(createIdentitiesLnk(singletonList("links"), username, offsetObj, limitObj).toLinks());
        list.add(createIdentitiesLnk(singletonList("items"), username, offsetObj, limitObj).toItems());
        return list;
    }

    public List<PubLink> lnkIdentitiesFPNL(List<String> includes, String username, Object offsetObj, Object limitObj) {
        List<PubLink> list = new ArrayList<>();
        list.add(createIdentitiesLnk(includes, username, offsetObj, limitObj).toFirst());
        list.add(createIdentitiesLnk(includes, username, offsetObj, limitObj).toPrev());
        list.add(createIdentitiesLnk(includes, username, offsetObj, limitObj).toNext());
        list.add(createIdentitiesLnk(includes, username, offsetObj, limitObj).toLast());
        return list;
    }

    public PubLink createIdentitiesLnk(List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        if (username == null) username = "";

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($identities)
                .queryParam("username", username);

        addOffsetAndLimit(builder, offsetObj, limitObj);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        String href = builder.toTemplate();
        return PubLink.create("identities", href);
    }

    public PubLink lnkIdentityById(IdentityEo user) {

        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($identities)
                .path($byId)
                .path(user == null ? "{id}" : user.getId())
                .toTemplate();

        return PubLink.create("identity", href, "GET: Fetch the specified identity.");
    }

    public PubLink lnkIdentityByUsername(IdentityEo user) {

        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($me)
                .path($identities)
                .path($byUsername)
                .path(user == null ? "{username}" : user.getUsername())
                .toTemplate();

        return PubLink.create("identity-by-username", href, "GET: Fetch the specified identity.");
    }

    public PubLink lnkAnonymous() {
        String href = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($anonymous)
                .toTemplate();

        return PubLink.create("identity", href, "GET: Fetch the specified identity.");
    }

    private void addOffsetAndLimit(UriBuilder builder, Object offsetObj, Object limitObj) {
        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, Identities.DEFAULT_LIMIT, "limit");
        builder.queryParam("offset", offset);
        builder.queryParam("limit", limit);
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

    public Response movedPermanently(PubLink link) {
        URI location = URI.create(link.getHref());
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(location).build();

    }
}
