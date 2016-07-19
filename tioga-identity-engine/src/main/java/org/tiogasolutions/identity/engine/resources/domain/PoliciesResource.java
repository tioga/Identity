package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.ResourceSupport;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.PolicyEo;
import org.tiogasolutions.identity.client.domain.IdentityPolicies;
import org.tiogasolutions.identity.client.domain.IdentityPolicy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class PoliciesResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public PoliciesResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPolicies(@QueryParam("username") String username,
                                @QueryParam("include") List<String> includes) {

        DomainProfileEo currentDomain = getKernel().getCurrentDomainProfile();
        IdentityPolicies pubPolicies = pubUtils.toPolicies(HttpStatusCode.OK, currentDomain, includes);
        return pubUtils.toResponse(pubPolicies).build();
    }

    @GET
    @Path("{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("policyId") String policyId) {

        DomainProfileEo currentDomain = getKernel().getCurrentDomainProfile();
        PolicyEo policy = currentDomain.findPolicyById(policyId);

        if (policy == null) {
            throw ApiException.notFound("The specified policy was not found.");
        }

        IdentityPolicy identityPolicy = pubUtils.toPolicy(HttpStatusCode.OK, policy);
        return pubUtils.toResponse(identityPolicy).build();
    }
}
