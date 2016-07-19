package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.domain.AuthenticationRequest;
import org.tiogasolutions.identity.client.domain.IdentityToken;
import org.tiogasolutions.identity.client.domain.IdentityTokens;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class TokensResource extends ResourceSupport {

    private final boolean anonymousApi;
    private final PubUtils pubUtils;

    public TokensResource(boolean anonymousApi, ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.anonymousApi = anonymousApi;
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(@QueryParam("include") List<String> includes) {
        DomainProfileEo domainProfile = getKernel().getCurrentDomainProfile();
        IdentityTokens pubTokens = pubUtils.toTokens(HttpStatusCode.OK, domainProfile, includes);
        return pubUtils.toResponse(pubTokens).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@PathParam("name") String name) {
        DomainProfileEo domainProfile = getKernel().getCurrentDomainProfile();
        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.OK, domainProfile, name);
        return pubUtils.toResponse(pubToken).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createToken(@FormParam("username") String username,
                                @FormParam("password") String password) {

        return createToken(new AuthenticationRequest(null, username, password));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createToken(AuthenticationRequest request) {
        DomainProfileEo domainProfile = getKernel().getCurrentDomainProfile();
        domainProfile = getKernel().createDomainToken(domainProfile.getDomainName(), request.getUsername());

        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domainProfile, request.getUsername());
        return pubUtils.toResponse(pubToken).build();
    }
}
