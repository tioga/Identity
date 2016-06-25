package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.$systems;
import static org.tiogasolutions.identity.kernel.constants.Paths.$users;

public class ClientResource {

    private final ClientStore clientStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public ClientResource(ExecutionManager<ClientEo> executionManager, ClientStore clientStore, IdentityPubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    private ClientEo getTenant() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTenant(@QueryParam("include") List<String> includes) {
        PubClient pubClient = pubUtils.toClient(HttpStatusCode.OK, getTenant());
        return pubUtils.toResponse(pubClient).build();
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
