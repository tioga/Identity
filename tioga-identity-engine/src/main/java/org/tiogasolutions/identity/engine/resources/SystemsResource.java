package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.client.PubSystems;
import org.tiogasolutions.identity.pub.client.PubUser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SystemsResource {

    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public SystemsResource(ExecutionManager<ClientEo> executionManager, IdentityPubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private ClientEo getTenant() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("username") String username,
                             @QueryParam("offset") String offset,
                             @QueryParam("limit") String limit,
                             @QueryParam("include") List<String> includes) {

        PubSystems pubSystems = pubUtils.toSystems(HttpStatusCode.OK, getTenant(), includes, offset, limit);
        return pubUtils.toResponse(pubSystems).build();
    }

    @GET
    @Path("{userId}")
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
