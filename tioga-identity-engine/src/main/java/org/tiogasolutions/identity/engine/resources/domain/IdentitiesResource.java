package org.tiogasolutions.identity.engine.resources.domain;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
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

public class IdentitiesResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<IdentityKernel> executionManager;

    public IdentitiesResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentities(@QueryParam("username") String username,
                                  @QueryParam("offset") String offsetStr,
                                  @QueryParam("limit") String limitStr,
                                  @QueryParam("include") List<String> includes) {

        ArrayList<IdentityEo> list = new ArrayList<>();
        if (StringUtils.isNotBlank(username)) {
            list.add(getKernel().findUserByName(username));

        } else {
            int offset = PubUtils.toInt(offsetStr, 0, "offset");
            int limit = PubUtils.toInt(limitStr, Identities.DEFAULT_LIMIT, "limit");
            list.addAll(getKernel().getAllIdentities(offset, limit));
        }

        DomainProfileEo domain = getKernel().getDomainProfile();
        Identities identities = pubUtils.toIdentities(HttpStatusCode.OK, domain, list, includes, username, offsetStr, limitStr);
        return pubUtils.toResponse(identities).build();
    }

    @GET
    @Path("{identityId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentity(@PathParam("identityId") String userId) {
        IdentityEo user = getKernel().findUserById(userId);

        if (user == null) {
            throw ApiException.notFound("The specified user was not found.");
        }

        DomainProfileEo domain = getKernel().getDomainProfile();
        Identity pubUser = pubUtils.toIdentity(HttpStatusCode.OK, domain, user);
        return pubUtils.toResponse(pubUser).build();
    }
}
