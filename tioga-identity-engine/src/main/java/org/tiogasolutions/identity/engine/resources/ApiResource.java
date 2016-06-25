package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.admin.AdminResource;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubClient;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.$admin;
import static org.tiogasolutions.identity.kernel.constants.Paths.$authenticate;
import static org.tiogasolutions.identity.kernel.constants.Paths.$client;

public class ApiResource {

    private final ClientStore clientStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public ApiResource(ExecutionManager<ClientEo> executionManager, IdentityPubUtils pubUtils, ClientStore clientStore) {
        this.pubUtils = pubUtils;
        this.clientStore = clientStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoot() {

        PubLinks links = new PubLinks();
        links.add("self", pubUtils.uriApi());
        links.add("status", pubUtils.uriRoot());
        links.add("authenticate", pubUtils.uriAuthenticate());
        links.add("client", pubUtils.uriClient());
        links.add("admin", pubUtils.uriAdmin());

        PubItem pubItem = new PubItem(HttpStatusCode.OK, links);
        return pubUtils.toResponse(pubItem).build();
    }

    @POST
    @Path($authenticate)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCreateToken(@FormParam("clientName") String clientName,
                                   @FormParam("password") String password) {

        ClientEo clientEo = clientStore.findByName(clientName);
        if (clientEo == null || objectsNotEqual(password, clientEo.getPassword())) {
            throw ApiException.unauthorized("Invalid username or password.");
        }

        clientEo.generateAccessToken();
        clientStore.update(clientEo);

        PubClient pubClient = pubUtils.toClient(HttpStatusCode.OK, clientEo);
        return pubUtils.toResponse(pubClient).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, clientStore);
    }

    @Path($client)
    public ClientResource getClientResource() {
        return new ClientResource(executionManager, clientStore, pubUtils);
    }
}

