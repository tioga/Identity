package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantProfileEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.tenant.PubTenant;
import org.tiogasolutions.identity.pub.tenant.PubUser;
import org.tiogasolutions.identity.pub.tenant.PubUsers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.tiogasolutions.identity.engine.support.IdentityPubUtils.UserBy;

public class ClientResource {

    private final ExecutionManager<TenantProfileEo> executionManager;
    private final IdentityPubUtils pubUtils;

    public ClientResource(ExecutionManager<TenantProfileEo> executionManager, IdentityPubUtils pubUtils) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
    }

    private TenantProfileEo getTenant() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTenant(@QueryParam("include") List<String> includes) {
        PubTenant pubTenant = pubUtils.toTenant(HttpStatusCode.OK, getTenant(), includes);
        return pubUtils.toResponse(pubTenant).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        PubUsers pubUsers = pubUtils.toUsers(HttpStatusCode.OK, getTenant().getUsers());
        return pubUtils.toResponse(pubUsers).build();
    }

    @GET
    @Path("/users/by-name/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersByName(@PathParam("username") String username) {
        UserEo user = getTenant().findUserByName(username);
        return getPubUserResponse(user, UserBy.NAME);
    }

    @GET
    @Path("/users/by-id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersById(@PathParam("id") String id) {
        UserEo user = getTenant().findUserById(id);
        return getPubUserResponse(user, UserBy.ID);
    }

    private Response getPubUserResponse(UserEo user, UserBy userBy) {
        PubUser pubUser = pubUtils.toUser(HttpStatusCode.OK, user, userBy);
        return pubUtils.toResponse(pubUser).build();
    }
}
