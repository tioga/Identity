package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubClient;
import org.tiogasolutions.identity.pub.client.PubClients;
import org.tiogasolutions.identity.pub.client.PubToken;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;

@RolesAllowed($ADMIN)
public class ClientsResource {

    private final ClientStore clientStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public ClientsResource(ExecutionManager<ClientEo> executionManager, ClientStore clientStore, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients(@QueryParam("offset") String offset,
                               @QueryParam("limit") String limit,
                               @QueryParam("include") List<String> includes) {
        List<ClientEo> clients = clientStore.getAll();
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubClients pubClients = pubUtils.toClients(sc, HttpStatusCode.OK, clients, includes, offset, limit);
        return pubUtils.toResponse(pubClients).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@PathParam("name") String name) {
        ClientEo client = clientStore.findByName(name);
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubClient pubClient = pubUtils.toClient(sc, HttpStatusCode.OK, client);
        return pubUtils.toResponse(pubClient).build();
    }

    @POST
    @Path("{name}/impersonate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response impersonate(@PathParam("name") String name) {

        ClientEo clientEo = clientStore.findByName(name);
        if (clientEo == null) {
            String msg = String.format("The client %s does not exist.", name);
            throw ApiException.notFound(msg);
        }

        clientEo.generateAccessToken(PubToken.ADMIN);
        clientStore.update(clientEo);

        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, clientEo, PubToken.ADMIN);
        return pubUtils.toResponse(pubToken).build();
    }
}
