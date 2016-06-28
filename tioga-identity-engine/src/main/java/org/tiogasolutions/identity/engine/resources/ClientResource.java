package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
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
    private final ExecutionManager<ClientEo> executionManager;

    public ClientResource(ExecutionManager<ClientEo> executionManager, ClientStore clientStore, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    private ClientEo getClient() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@QueryParam("include") List<String> includes) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubClient pubClient = pubUtils.toClient(sc, HttpStatusCode.OK, getClient());
        return pubUtils.toResponse(pubClient).build();
    }

    @GET
    @Path("token/{name}")
    public Response getToken(@PathParam("name") String name) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, getClient(), name);
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
