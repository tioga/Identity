package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLink;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.tenant.PubTenant;
import org.tiogasolutions.identity.pub.tenant.PubUser;
import org.tiogasolutions.identity.pub.tenant.PubUsers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

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

    public PubTenant toTenant(HttpStatusCode statusCode, TenantEo tenant, List<String> includes) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();
        links.add("self",               getTenantUri(includes));
        links.add("self-no-users",      getTenantUri(emptyList()));
        links.add("self-user-items",   getTenantUri(singletonList("user-items")));
        links.add("self-user-links",   getTenantUri(singletonList("user-links")));
        links.add("users",              getTenantUsersUri(null, null, 0, PubUsers.DEFAULT_LIMIT));
        links.add("users-items",        getTenantUsersUri(singletonList("items"), null, 0, PubUsers.DEFAULT_LIMIT));
        links.add("users-links",        getTenantUsersUri(singletonList("links"), null, 0, PubUsers.DEFAULT_LIMIT));
        links.add("api",                getApiUri());

        return new PubTenant(
                toPubStatus(statusCode),
                links,
                tenant.getId(),
                tenant.getRevision(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getDbName());
    }

    public PubUsers toUsers(HttpStatusCode statusCode, List<UserEo> users, List<String> includes, String username, Object offset, Object limit) {
        if (includes == null) includes = Collections.emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       getTenantUsersUri(includes, username, offset, limit));
        links.add("self-items", getTenantUsersUri(singletonList("items"), username, offset, limit));
        links.add("self-links", getTenantUsersUri(singletonList("links"), username, offset, limit));

        links.add("user",   getTenantUserByIdUri("{id}"));
        links.add("tenant", getTenantUri(null));
        links.add("api",    getApiUri());

        if (users.size() > 0) {
            UserEo first = users.get(0);
            links.add("first-user", getTenantUserByIdUri(first.getId()));
        }

        links.add("first", getTenantUsersUri(null, username, 0, limit));
        links.add("prev",  getTenantUsersUri(null, username, 0, limit));
        links.add("next",  getTenantUsersUri(null, username, 0, limit));
        links.add("last",  getTenantUsersUri(null, username, 0, limit));

        List<PubUser> usersList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserEo user = users.get(i);
            PubUser pubUser = toUser(null, user);
            usersList.add(pubUser);
            linksList.add(pubUser.get_links().get("self"));
        }

        return new PubUsers(
                toPubStatus(statusCode),
                links,
                users.size(),
                users.size(),
                0,
                999999999,
                includes.contains("items") ? usersList : null,
                includes.contains("links") ? linksList : null);
    }

    public PubUser toUser(HttpStatusCode statusCode, UserEo user) {

        PubLinks links = new PubLinks();
        links.add("self", getTenantUserByIdUri(user.getId()));

        return new PubUser(
            toPubStatus(statusCode),
            links,
            user.getId(),
            user.getRevision(),
            user.getUsername(),
            user.getPassword(),
            user.getAssignedRoles()
        );
    }

    private PubStatus toPubStatus(HttpStatusCode statusCode) {
        return statusCode == null ? null : new PubStatus(statusCode);
    }

    public String getRootUri() {
        return uriInfo.getBaseUriBuilder().toTemplate();
    }

    public String getApiUri() {
        return uriInfo.getBaseUriBuilder().path("api").toTemplate();
    }

    public String getAdminUri() {
        return uriInfo.getBaseUriBuilder().path("api/admin").toTemplate();
    }

    public String getTenantUri(List<String> includes) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder().path("api/tenant");
        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String getTenantUsersUri(List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, PubUsers.DEFAULT_LIMIT, "limit");
        if (username == null) username = "";

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("api/tenant/users")
                .queryParam("username", username)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String getTenantUserByIdUri(String id) {
        return uriInfo.getBaseUriBuilder().path("api/tenant/users").path(id).toTemplate();
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
