package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.identity.kernel.constants.Paths.$clients;

public class AdminResource {

    private final ExecutionManager<ClientEo> executionManager;
    private final PubUtils pubUtils;
    private final ClientStore clientStore;

    public AdminResource(ExecutionManager<ClientEo> executionManager, PubUtils pubUtils, ClientStore clientStore) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.uriAdmin());
        pubLinks.add("clients", pubUtils.uriClients(null, null, null));
        pubLinks.add("api", pubUtils.uriApi());
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

    @Path($clients)
    public ClientsResource getClientsResource() {
        return new ClientsResource(executionManager, clientStore, pubUtils);
    }
}
