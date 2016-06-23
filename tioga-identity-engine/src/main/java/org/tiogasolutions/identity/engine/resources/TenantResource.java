package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.tenant.PubTenant;
import org.tiogasolutions.identity.pub.tenant.PubUser;
import org.tiogasolutions.identity.pub.tenant.PubUsers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class TenantResource {

    private final ExecutionManager<TenantEo> executionManager;
    private final IdentityPubUtils pubUtils;

    public TenantResource(ExecutionManager<TenantEo> executionManager, IdentityPubUtils pubUtils) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
    }

    private TenantEo getTenant() {
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
    public Response getUsers(@QueryParam("username") String username,
                             @QueryParam("offset") String offset,
                             @QueryParam("limit") String limit,
                             @QueryParam("include") List<String> includes) {

        PubUsers pubUsers = pubUtils.toUsers(HttpStatusCode.OK, getTenant().getUsers(username), includes, username, offset, limit);
        return pubUtils.toResponse(pubUsers).build();
    }

    @GET
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userId") String userId) {
        UserEo user = getTenant().findUserById(userId);
        if (user == null) {
            throw ApiException.notFound("The specified user was not found.");
        }
        PubUser pubUser = pubUtils.toUser(HttpStatusCode.OK, user);
        return pubUtils.toResponse(pubUser).build();
    }
}
