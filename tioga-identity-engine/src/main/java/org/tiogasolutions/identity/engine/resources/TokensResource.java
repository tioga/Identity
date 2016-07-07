package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.client.domain.IdentityToken;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class TokensResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public TokensResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Path("{name}")
    public Response getToken(@PathParam("name") String name) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.OK, getKernel().getDomainProfile(), name);
        return pubUtils.toResponse(pubToken).build();
    }
}
