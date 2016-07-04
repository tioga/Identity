package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.admin.AdminResource;
import org.tiogasolutions.identity.engine.resources.domain.RealmsResource;
import org.tiogasolutions.identity.engine.resources.domain.RolesResource;
import org.tiogasolutions.identity.engine.resources.domain.SystemsResource;
import org.tiogasolutions.identity.engine.resources.domain.UsersResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.client.PubToken;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;

public class ApiResource {

    private final ClientStore clientStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public ApiResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils, ClientStore clientStore) {
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

        clientEo.generateAccessToken(PubToken.DEFAULT);
        clientStore.update(clientEo);

        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, clientEo, PubToken.DEFAULT);
        return pubUtils.toResponse(pubToken).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, clientStore);
    }

    @Path($client)
    public ClientResource getClientResource() {
        return new ClientResource(executionManager, clientStore, pubUtils);
    }

    @Path($users)
    public UsersResource getUsersResource() {
        return new UsersResource(executionManager, pubUtils);
    }

    @Path($systems)
    public SystemsResource getSystemsResource() {
        return new SystemsResource(executionManager, pubUtils);
    }

    @Path($realms)
    public RealmsResource getRealmsResource() {
        return new RealmsResource(executionManager, pubUtils);
    }

    @Path($roles)
    public RolesResource getRolesResource() {
        return new RolesResource(executionManager, pubUtils);
    }
}

