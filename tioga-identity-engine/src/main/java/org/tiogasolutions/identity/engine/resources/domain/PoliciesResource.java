package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.PolicyEo;
import org.tiogasolutions.identity.pub.PubPolicies;
import org.tiogasolutions.identity.pub.PubPolicy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class PoliciesResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public PoliciesResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("username") String username,
                             @QueryParam("offset") String offset,
                             @QueryParam("limit") String limit,
                             @QueryParam("include") List<String> includes) {

        PubPolicies pubPolicies = pubUtils.toPolicies(HttpStatusCode.OK, getKernel().getDomainProfile(), includes, offset, limit);
        return pubUtils.toResponse(pubPolicies).build();
    }

    @GET
    @Path("{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("policyId") String policyId) {
        PolicyEo policy = getKernel().getDomainProfile().findPolicyById(policyId);
        if (policy == null) {
            throw ApiException.notFound("The specified policy was not found.");
        }
        PubPolicy pubPolicy = pubUtils.toPolicy(HttpStatusCode.OK, policy);
        return pubUtils.toResponse(pubPolicy).build();
    }
}
