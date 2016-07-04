package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.domain.SystemsResource;
import org.tiogasolutions.identity.engine.resources.domain.UsersResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubClient;
import org.tiogasolutions.identity.pub.client.PubToken;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.$systems;
import static org.tiogasolutions.identity.kernel.constants.Paths.$users;

public class ClientResource {

    private final ClientStore clientStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public ClientResource(ExecutionManager<IdentityKernel> executionManager, ClientStore clientStore, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@QueryParam("include") List<String> includes) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubClient pubClient = pubUtils.toClient(sc, HttpStatusCode.OK, getKernel().getClient());
        return pubUtils.toResponse(pubClient).build();
    }

    @GET
    @Path("token/{name}")
    public Response getToken(@PathParam("name") String name) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, getKernel().getClient(), name);
        return pubUtils.toResponse(pubToken).build();
    }

    @Path($users)
    public UsersResource getUsersResource() {
        return new UsersResource(executionManager, pubUtils);
    }

    @Path($systems)
    public SystemsResource getSystemsResource() {
        return new SystemsResource(executionManager, pubUtils);
    }

}
