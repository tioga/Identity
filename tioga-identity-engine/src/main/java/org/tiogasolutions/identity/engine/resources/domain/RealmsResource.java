package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.domain.RealmEo;
import org.tiogasolutions.identity.pub.client.PubRealm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RealmsResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public RealmsResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
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
    @Path("{realmId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("realmId") String realmId) {
        RealmEo realm = getKernel().getClient().findRealmById(realmId);
        if (realm == null) {
            throw ApiException.notFound("The specified realm was not found.");
        }

        PubRealm pubRealm = pubUtils.toRealm(HttpStatusCode.OK, realm);
        return pubUtils.toResponse(pubRealm).build();
    }
}
