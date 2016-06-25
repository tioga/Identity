package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.kernel.domain.SystemEo;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLink;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.tenant.*;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;

public class IdentityPubUtils {

    private final UriInfo uriInfo;

    public IdentityPubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public IdentityPubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public Response.ResponseBuilder toResponse(PubItem pubItem) {
        return Response.status(pubItem.get_status().getCode()).entity(pubItem);
    }





    public PubTenant toTenant(HttpStatusCode statusCode, TenantEo tenant) {

        PubLinks links = new PubLinks();
        links.add("self",               uriTenantByName(tenant));
        links.add("systems",            uriSystems(tenant.getName(), null, null, null));

        return new PubTenant(
                toStatus(statusCode),
                links,
                tenant.getName(),
                tenant.getRevision(),
                tenant.getStatus(),
                tenant.getAuthorizationToken(),
                tenant.getPassword(),
                tenant.getDbName());
    }

    public PubTenants toTenants(HttpStatusCode statusCode, List<TenantEo> tenants, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = Collections.emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriTenants(includes, offset, limit));
        links.add("self-items", uriTenants(singletonList("items"), offset, limit));
        links.add("self-links", uriTenants(singletonList("links"), offset, limit));

        if (tenants.size() > 0) {
            TenantEo first = tenants.get(0);
            links.add("first-tenant", uriTenantByName(first));
        }

        links.add("first", uriTenants(includes, offset, limit));
        links.add("prev",  uriTenants(includes, offset, limit));
        links.add("next",  uriTenants(includes, offset, limit));
        links.add("last",  uriTenants(includes, offset, limit));

        List<PubTenant> tenantsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < tenants.size(); i++) {
            TenantEo tenant = tenants.get(i);
            PubTenant pubUser = toTenant(null, tenant);
            tenantsList.add(pubUser);
            linksList.add(pubUser.get_links().get("self"));
        }

        return new PubTenants(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                999999999,
                includes.contains("items") ? tenantsList : null,
                includes.contains("links") ? linksList : null);
    }





    public PubSystem toSystem(HttpStatusCode statusCode, SystemEo system) {

        PubLinks links = new PubLinks();
        links.add("self", uriSystemById(system));

        return new PubSystem(
                toStatus(statusCode),
                links,
                system.getName(),
                system.getTenantName()
        );
    }

    public PubSystems toSystems(HttpStatusCode statusCode, TenantEo tenant, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = Collections.emptyList();

        PubLinks links = new PubLinks();
        List<SystemEo> systems = tenant.getSystems();

        links.add("self",       uriSystems(tenant.getName(), includes, offset, limit));
        links.add("self-items", uriSystems(tenant.getName(), singletonList("items"), offset, limit));
        links.add("self-links", uriSystems(tenant.getName(), singletonList("links"), offset, limit));


        if (systems.size() > 0) {
            SystemEo first = systems.get(0);
            links.add("first-system", uriSystemById(first));
        }

        links.add("first", uriSystems(tenant.getName(), null, 0, limit));
        links.add("prev",  uriSystems(tenant.getName(), null, 0, limit));
        links.add("next",  uriSystems(tenant.getName(), null, 0, limit));
        links.add("last",  uriSystems(tenant.getName(), null, 0, limit));

        List<PubSystem> itemsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < systems.size(); i++) {
            SystemEo system = systems.get(i);
            PubSystem pubSystem = toSystem(null, system);
            itemsList.add(pubSystem);
            linksList.add(pubSystem.get_links().get("self"));
        }

        return new PubSystems(
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
        links.add("self", uriUserById(user.getId()));

        return new PubUser(
                toStatus(statusCode),
                links,
                user.getId(),
                user.getRevision(),
                user.getUsername(),
                user.getPassword(),
                user.getAssignedRoles()
        );
    }

    public PubUsers toUsers(HttpStatusCode statusCode, List<UserEo> users, List<String> includes, String username, Object offset, Object limit) {
        if (includes == null) includes = Collections.emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriUsers(includes, username, offset, limit));
        links.add("self-items", uriUsers(singletonList("items"), username, offset, limit));
        links.add("self-links", uriUsers(singletonList("links"), username, offset, limit));

        links.add("user",   uriUserById("{id}"));
        links.add("api",    uriApi());

        if (users.size() > 0) {
            UserEo first = users.get(0);
            links.add("first-user", uriUserById(first.getId()));
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
            linksList.add(pubUser.get_links().get("self"));
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
        return uriInfo.getBaseUriBuilder().path($api_v1).toTemplate();
    }

    public String uriAdmin() {
        return uriInfo.getBaseUriBuilder().path("api/admin").toTemplate();
    }

    public String uriTenants(List<String> includes, Object offset, Object limit) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder().path($api_v1).path($tenants);
        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriTenantByName(TenantEo tenant) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($tenants)
                .path(tenant.getName())
                .toTemplate();
    }

    public String uriSystemById(SystemEo first) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($tenants)
                .path(first.getTenantName())
                .path($systems)
                .path(first.getId())
                .toTemplate();
    }

    private String uriSystems(String tenantName, List<String> includes, Object offset, Object limit) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder().path($api_v1).path($tenants).path(tenantName).path("systems");
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
                .path($api_v1).path($tenants).path($users)
                .queryParam("username", username)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUserById(String id) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1).path($tenants).path($users)
                .path(id)
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
