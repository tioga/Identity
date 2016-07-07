package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.domain.PoliciesResource;
import org.tiogasolutions.identity.engine.resources.domain.IdentitiesResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.client.domain.IdentityDomain;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.$identities;
import static org.tiogasolutions.identity.kernel.constants.Paths.$policies;
import static org.tiogasolutions.identity.kernel.constants.Paths.$tokens;

public class MeResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public MeResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomainProfile(@QueryParam("include") List<String> includes) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        IdentityDomain identityDomain = pubUtils.toDomainProfile(sc, HttpStatusCode.OK, getKernel().getDomainProfile());
        return pubUtils.toResponse(identityDomain).build();
    }

    @Path($tokens)
    public TokensResource getTokensResource() {
        return new TokensResource(executionManager, pubUtils);
    }

    @Path($identities)
    public IdentitiesResource getIdentitiesResource() {
        return new IdentitiesResource(executionManager, pubUtils);
    }

    @Path($policies)
    public PoliciesResource getPoliciesResource() {
        return new PoliciesResource(executionManager, pubUtils);
    }

}
