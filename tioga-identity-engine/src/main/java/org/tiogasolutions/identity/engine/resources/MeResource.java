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

public class MeResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public MeResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomainProfile(@QueryParam("include") List<String> includes) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        IdentityDomain identityDomain = pubUtils.toDomainProfile(sc, HttpStatusCode.OK, getKernel().getCurrentDomainProfile());
        return pubUtils.toResponse(identityDomain).build();
    }

    @Path($tokens)
    public TokensResource getTokensResource() {
        return new TokensResource(false, executionManager, pubUtils);
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
