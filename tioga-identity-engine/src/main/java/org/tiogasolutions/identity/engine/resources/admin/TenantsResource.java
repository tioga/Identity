package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubClients;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;

public class TenantsResource {

    private final ClientStore clientStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public TenantsResource(ExecutionManager<ClientEo> executionManager, ClientStore clientStore, IdentityPubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    @GET
    @RolesAllowed($ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTenants(@QueryParam("offset") String offset,
                               @QueryParam("limit") String limit,
                               @QueryParam("include") List<String> includes) {
        List<ClientEo> clients = clientStore.getAll();
        PubClients pubClients = pubUtils.toClients(HttpStatusCode.OK, clients, includes, offset, limit);
        return pubUtils.toResponse(pubClients).build();
    }
}
