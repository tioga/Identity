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
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.pub.PubToken;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;

public class ApiResource {

    private final DomainStore domainStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public ApiResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils, DomainStore domainStore) {
        this.pubUtils = pubUtils;
        this.domainStore = domainStore;
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
    public Response getCreateToken(@FormParam("username") String domainName,
                                   @FormParam("password") String password) {

        DomainProfileEo domainProfile = domainStore.findByName(domainName);
        if (domainProfile == null || objectsNotEqual(password, domainProfile.getPassword())) {
            throw ApiException.unauthorized("Invalid username or password.");
        }

        domainProfile.generateAccessToken(PubToken.DEFAULT);
        domainStore.update(domainProfile);

        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domainProfile, PubToken.DEFAULT);
        return pubUtils.toResponse(pubToken).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, domainStore);
    }

    @Path($client)
    public ClientResource getClientResource() {
        return new ClientResource(executionManager, pubUtils);
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

