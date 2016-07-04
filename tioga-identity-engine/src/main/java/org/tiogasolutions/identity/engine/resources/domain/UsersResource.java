package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.pub.PubUser;
import org.tiogasolutions.identity.pub.PubUsers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class UsersResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public UsersResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("username") String username,
                             @QueryParam("offset") String offset,
                             @QueryParam("limit") String limit,
                             @QueryParam("include") List<String> includes) {

        ArrayList<UserEo> users = new ArrayList<>();
        users.add(getKernel().findUserByName(username));

        PubUsers pubUsers = pubUtils.toUsers(HttpStatusCode.OK, users, includes, username, offset, limit);
        return pubUtils.toResponse(pubUsers).build();
    }

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userId") String userId) {
        UserEo user = getKernel().findUserById(userId);
        if (user == null) {
            throw ApiException.notFound("The specified user was not found.");
        }
        PubUser pubUser = pubUtils.toUser(HttpStatusCode.OK, user);
        return pubUtils.toResponse(pubUser).build();
    }
}
