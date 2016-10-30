package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.ResourceSupport;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.client.domain.IdentityDomain;
import org.tiogasolutions.identity.client.domain.IdentityDomains;
import org.tiogasolutions.identity.client.domain.IdentityToken;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;

@RolesAllowed($ADMIN)
public class DomainsResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public DomainsResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomains(@QueryParam("offset") String offset,
                               @QueryParam("limit") String limit,
                               @QueryParam("include") List<String> includes) {
        List<DomainProfileEo> domains = getKernel().getAllDomains();
        IdentityDomains pubDomains = pubUtils.toDomains(HttpStatusCode.OK, domains, includes, offset, limit);
        return pubUtils.toResponse(pubDomains).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomain(@PathParam("name") String name) {
        DomainProfileEo domainEo = getKernel().findDomainByName(name);
        SecurityContext sc = getSecurityContext();
        IdentityDomain identityDomain = pubUtils.toDomainProfile(sc, HttpStatusCode.OK, domainEo);
        return pubUtils.toResponse(identityDomain).build();
    }

    @POST
    @Path("{name}/impersonate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response impersonate(@PathParam("name") String name) {

        DomainProfileEo domainProfile = getKernel().findDomainByName(name);
        if (domainProfile == null) {
            String msg = String.format("The domain %s does not exist.", name);
            throw ApiException.notFound(msg);
        }

        String tokenName = "tioga-solutions-admin";

        domainProfile.generateAccessToken(tokenName);
        getKernel().update(domainProfile);

        IdentityToken pubToken = pubUtils.toToken(HttpStatusCode.CREATED, domainProfile, tokenName);
        return pubUtils.toResponse(pubToken).build();
    }
}
