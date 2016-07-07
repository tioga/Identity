package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.admin.AdminResource;
import org.tiogasolutions.identity.engine.resources.domain.RealmsResource;
import org.tiogasolutions.identity.engine.resources.domain.RolesResource;
import org.tiogasolutions.identity.engine.resources.domain.PoliciesResource;
import org.tiogasolutions.identity.engine.resources.domain.UsersResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;
import org.tiogasolutions.identity.kernel.domain.RealmEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;
import org.tiogasolutions.identity.pub.PubToken;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;

public class ApiResource {

    private final IdentityStore identityStore;
    private final DomainStore domainStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public ApiResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils, DomainStore domainStore, IdentityStore identityStore) {
        this.pubUtils = pubUtils;
        this.identityStore = identityStore;
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
        links.add("me", pubUtils.uriMe());
        links.add("admin", pubUtils.uriAdmin());

        PubItem pubItem = new PubItem(HttpStatusCode.OK, links);
        return pubUtils.toResponse(pubItem).build();
    }

    @POST
    @Path($authenticate)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCreateToken(@FormParam("realm") String realmName,
                                   @FormParam("username") String username,
                                   @FormParam("password") String password) {

        // We use our internal domain to authenticate you.
        DomainProfileEo internalDomain = domainStore.findByName(DomainProfileEo.INTERNAL_DOMAIN);

        // Now let's see if this user exists within our domain.
        IdentityEo identity = identityStore.findUserByName(internalDomain, username);
        if (identity == null || objectsNotEqual(password, identity.getPassword())) {
            throw ApiException.unauthorized("Invalid username or password.");
        }

        RealmEo realmEo = internalDomain.findRealmByName(realmName);
        if (realmEo == null) {
            // You don't have access because it doesn't exist.
            throw ApiException.forbidden("Access to this realm is forbidden.");
        }

        if (identity.permits(realmEo) == false) {
            throw ApiException.forbidden("Access to this realm is forbidden.");
        }

        // The realm name that the user logged into is the domain
        // name that we are actually updating the api key for.
        DomainProfileEo domain = domainStore.findByName(realmName);
        String tokenName = username;
        domain.generateAccessToken(tokenName);
        domainStore.update(domain);

        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domain, tokenName);
        return pubUtils.toResponse(pubToken).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, domainStore);
    }

    @Path($me)
    public MeResource getMeResource() {
        return new MeResource(executionManager, pubUtils);
    }

    @Path($users)
    public UsersResource getUsersResource() {
        return new UsersResource(executionManager, pubUtils);
    }

    @Path($policies)
    public PoliciesResource getPoliciesResource() {
        return new PoliciesResource(executionManager, pubUtils);
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

