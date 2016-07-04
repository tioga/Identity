package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.domain.PoliciesResource;
import org.tiogasolutions.identity.engine.resources.domain.UsersResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.pub.PubDomain;
import org.tiogasolutions.identity.pub.PubToken;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Paths.$policies;
import static org.tiogasolutions.identity.kernel.constants.Paths.$users;

public class ClientResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public ClientResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
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
        PubDomain pubDomain = pubUtils.toDomainProfile(sc, HttpStatusCode.OK, getKernel().getDomainProfile());
        return pubUtils.toResponse(pubDomain).build();
    }

    @GET
    @Path("token/{name}")
    public Response getToken(@PathParam("name") String name) {
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, getKernel().getDomainProfile(), name);
        return pubUtils.toResponse(pubToken).build();
    }

    @Path($users)
    public UsersResource getUsersResource() {
        return new UsersResource(executionManager, pubUtils);
    }

    @Path($policies)
    public PoliciesResource getPoliciesResource() {
        return new PoliciesResource(executionManager, pubUtils);
    }

}
