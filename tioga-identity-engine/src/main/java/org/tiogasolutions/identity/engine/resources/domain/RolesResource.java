package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.domain.RoleEo;
import org.tiogasolutions.identity.pub.client.PubRole;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RolesResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public RolesResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response findUserByName(@QueryParam("username") String username,
//                             @QueryParam("offset") String offset,
//                             @QueryParam("limit") String limit,
//                             @QueryParam("include") List<String> includes) {
//
//        PubSystems pubSystems = pubUtils.toSystems(HttpStatusCode.OK, getClient(), includes, offset, limit);
//        return pubUtils.toResponse(pubSystems).build();
//    }

    @GET
    @Path("{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("roleId") String roleId) {
        RoleEo role = getKernel().getClient().findRoleById(roleId);
        if (role == null) {
            throw ApiException.notFound("The specified role was not found.");
        }

        PubRole pubRole = pubUtils.toRole(HttpStatusCode.OK, role);
        return pubUtils.toResponse(pubRole).build();
    }
}
