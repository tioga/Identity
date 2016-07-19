package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.domain.AuthenticationRequest;
import org.tiogasolutions.identity.client.domain.IdentityInfo;
import org.tiogasolutions.identity.client.domain.IdentityToken;
import org.tiogasolutions.identity.engine.grizzly.IdentityMain;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;
import org.tiogasolutions.identity.kernel.domain.RealmEo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.$authentication;
import static org.tiogasolutions.identity.kernel.constants.Paths.$info;
import static org.tiogasolutions.identity.kernel.constants.Paths.$tokens;

public class AnonymousResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public AnonymousResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    public Response getRoot() throws Exception {
        return pubUtils.movedPermanently(pubUtils.lnkApiV1());
    }

    @GET
    @Path($info)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() throws Exception {

        PubLinks links = PubLinks.self(pubUtils.lnkAnonymousInfo());
        links.add(pubUtils.lnkAnonymousTokens());
        links.add(pubUtils.lnkAdmin());
        links.add(pubUtils.lnkMe());

        long elapsed = System.currentTimeMillis() - IdentityMain.startedAt;
        IdentityInfo identityInfo = new IdentityInfo(HttpStatusCode.OK, links, elapsed);

        return pubUtils.toResponse(identityInfo).build();
    }

    @GET
    @Path($authentication)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authentication(@QueryParam("domainName") String domainName,
                                   @QueryParam("username") String username,
                                   @QueryParam("password") String password) {

        AuthenticationRequest request = new AuthenticationRequest(domainName, username, password);
        authenticate(request); // ...and you are?

        DomainProfileEo domainProfile = getKernel().findDomainByName(domainName);
        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.OK, domainProfile, username);
        return pubUtils.toResponse(pubToken).build();
    }

    @POST
    @Path($tokens)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createToken(@FormParam("domainName") String domainName,
                                @FormParam("username") String username,
                                @FormParam("password") String password) {

        return createToken(new AuthenticationRequest(domainName, username, password));
    }

    @POST
    @Path($tokens)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createToken(AuthenticationRequest request) {

        authenticate(request); // ...and you are?

        // Now that the user is authenticated, we can go ahead and create the token.
        IdentityKernel kernel = executionManager.getContext().getDomain();
        DomainProfileEo domainProfile = kernel.createDomainToken(request.getDomainName(), request.getUsername());

        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domainProfile, request.getUsername());
        return pubUtils.toResponse(pubToken).build();
    }

    private void authenticate(AuthenticationRequest request) {

        IdentityKernel kernel = executionManager.getContext().getDomain();

        // We use our internal domain to authenticate you.
        DomainProfileEo internalDomain = kernel.findDomainByName(DomainProfileEo.INTERNAL_DOMAIN);

        // Now let's see if this user exists within our domain.
        IdentityEo identity = kernel.findIdentityByUsername(internalDomain, request.getUsername());
        if (identity == null || objectsNotEqual(request.getPassword(), identity.getPassword())) {
            throw ApiException.unauthorized("Invalid domainName, username or password. Values must be passed as x-www-form-urlencoded parameters or as part of a JSON object.");
        }

        RealmEo realmEo = internalDomain.findRealmByName(request.getDomainName());
        if (realmEo == null) {
            // You don't have access because it doesn't exist.
            throw ApiException.forbidden("Access to this realm is forbidden.");
        }

        if (identity.permits(realmEo) == false) {
            throw ApiException.forbidden("Access to this realm is forbidden.");
        }
    }
}
