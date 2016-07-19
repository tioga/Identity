package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.resources.ResourceSupport;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;
import org.tiogasolutions.identity.client.domain.Identity;
import org.tiogasolutions.identity.client.domain.Identities;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class IdentitiesResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public IdentitiesResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentities(@QueryParam("username") String username,
                                  @QueryParam("offset") String offsetStr,
                                  @QueryParam("limit") String limitStr,
                                  @QueryParam("include") List<String> includes) {

        DomainProfileEo currentDomain = getKernel().getCurrentDomainProfile();
        ArrayList<IdentityEo> list = new ArrayList<>();

        if (StringUtils.isNotBlank(username)) {
            list.add(getKernel().findIdentityByUsername(currentDomain, username));

        } else {
            int offset = PubUtils.toInt(offsetStr, 0, "offset");
            int limit = PubUtils.toInt(limitStr, Identities.DEFAULT_LIMIT, "limit");
            list.addAll(getKernel().getAllIdentities(currentDomain, offset, limit));
        }

        Identities identities = pubUtils.toIdentities(HttpStatusCode.OK, currentDomain, list, includes, username, offsetStr, limitStr);
        return pubUtils.toResponse(identities).build();
    }

    @GET
    @Path("/by-username/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentityByUsername(@PathParam("username") String username) {

        DomainProfileEo domainProfile = getKernel().getCurrentDomainProfile();
        IdentityEo identityEo = getKernel().findIdentityByUsername(domainProfile, username);

        if (identityEo == null) {
            throw ApiException.notFound("The specified identity was not found.");
        }

        DomainProfileEo currentDomain = getKernel().getCurrentDomainProfile();
        Identity identity = pubUtils.toIdentity(HttpStatusCode.OK, currentDomain, identityEo);
        return pubUtils.toResponse(identity).build();
    }

    @GET
    @Path("/by-id/{identityId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentityById(@PathParam("identityId") String userId) {
        IdentityEo user = getKernel().findIdentityById(userId);

        if (user == null) {
            throw ApiException.notFound("The specified user was not found.");
        }

        DomainProfileEo currentDomain = getKernel().getCurrentDomainProfile();
        Identity pubUser = pubUtils.toIdentity(HttpStatusCode.OK, currentDomain, user);
        return pubUtils.toResponse(pubUser).build();
    }
}
