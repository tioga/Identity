package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.pub.PubDomain;
import org.tiogasolutions.identity.pub.PubDomains;
import org.tiogasolutions.identity.pub.PubToken;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;

@RolesAllowed($ADMIN)
public class DomainsResource {

    private final DomainStore domainStore;
    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public DomainsResource(ExecutionManager<IdentityKernel> executionManager, DomainStore domainStore, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.domainStore = domainStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomains(@QueryParam("offset") String offset,
                               @QueryParam("limit") String limit,
                               @QueryParam("include") List<String> includes) {
        List<DomainProfileEo> domains = domainStore.getAll();
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubDomains pubDomains = pubUtils.toDomains(HttpStatusCode.OK, domains, includes, offset, limit);
        return pubUtils.toResponse(pubDomains).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomain(@PathParam("name") String name) {
        DomainProfileEo domainEo = domainStore.findByName(name);
        SecurityContext sc = executionManager.getContext().getSecurityContext();
        PubDomain pubDomain = pubUtils.toDomainProfile(sc, HttpStatusCode.OK, domainEo);
        return pubUtils.toResponse(pubDomain).build();
    }

    @POST
    @Path("{name}/impersonate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response impersonate(@PathParam("name") String name) {

        DomainProfileEo domainProfile = domainStore.findByName(name);
        if (domainProfile == null) {
            String msg = String.format("The domain %s does not exist.", name);
            throw ApiException.notFound(msg);
        }

        domainProfile.generateAccessToken(PubToken.ADMIN);
        domainStore.update(domainProfile);

        PubToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domainProfile, PubToken.ADMIN);
        return pubUtils.toResponse(pubToken).build();
    }
}
