package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.kernel.domain.TenantProfileEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.tenant.PubTenant;
import org.tiogasolutions.identity.pub.tenant.PubUser;
import org.tiogasolutions.identity.pub.tenant.PubUsers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IdentityPubUtils {

    public enum UserBy {ID, NAME}

    private final UriInfo uriInfo;
    private final IdentityUriUtils uris;

    public IdentityPubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        this.uris = new IdentityUriUtils(uriInfo);
    }

    public IdentityPubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
        this.uris = new IdentityUriUtils(uriInfo);
    }

    public IdentityUriUtils getUris() {
        return uris;
    }

    public Response.ResponseBuilder toResponse(PubItem pubItem) {
        return Response.status(pubItem.get_status().getCode()).entity(pubItem);
    }

    public PubTenant toTenant(HttpStatusCode statusCode, TenantProfileEo profile, List<String> includes) {
        PubUsers pubUsers = includes.contains("users") ? toUsers(null, profile.getUsers()) : null;

        PubLinks links = new PubLinks();
        links.add("self",    uris.getClient());
        links.add("api",     uris.getApi());
        links.add("users",   uris.getClientUsers());

        return new PubTenant(
                toPubStatus(statusCode),
                links,
                profile.getProfileId(),
                profile.getRevision(),
                profile.getTenantName(),
                profile.getTenantStatus(),
                profile.getTenantDbName(),
                pubUsers);
    }

    public PubUsers toUsers(HttpStatusCode statusCode, Collection<UserEo> users) {

        PubLinks links = new PubLinks();

        links.add("self",         uris.getClientUsers());
        links.add("user-by-id",   uris.getClientUserById("{id}"));
        links.add("user-by-name", uris.getClientUserByName("{name}"));
        links.add("client",       uris.getClient());
        links.add("api",          uris.getApi());

        links.add("first", uris.getClientUsers());
        links.add("prev", uris.getClientUsers());
        links.add("next", uris.getClientUsers());
        links.add("last", uris.getClientUsers());

        List<PubUser> list = new ArrayList<>();
        for (UserEo user : users) {
            list.add(toUser(null, user, UserBy.ID));
        }

        return new PubUsers(
                toPubStatus(statusCode),
                links,
                users.size(),
                users.size(),
                0,
                999999999,
                list);
    }

    public PubUser toUser(HttpStatusCode statusCode, UserEo user, UserBy userBy) {

        PubLinks links = new PubLinks();
        if (UserBy.ID == userBy) {
            links.add("self", uris.getClientUserById(user.getId()));
            links.add("by-name", uris.getClientUserByName(user.getUsername()));
        }
        if (UserBy.NAME == userBy) {
            links.add("self", uris.getClientUserByName(user.getUsername()));
            links.add("by-id", uris.getClientUserById(user.getId()), "User By ID");
        }

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
}
