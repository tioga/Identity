package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.engine.resources.admin.AdminResource;
import org.tiogasolutions.identity.engine.resources.domain.IdentitiesResource;
import org.tiogasolutions.identity.engine.resources.domain.PoliciesResource;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.tiogasolutions.identity.kernel.constants.Paths.*;

public class ApiResource extends ResourceSupport {

    private final PubUtils pubUtils;

    public ApiResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils) {
        super(executionManager);
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoot() {

        PubLinks links = PubLinks.self(pubUtils.lnkApiV1());
        //links.add(pubUtils.lnkApiV1());
        links.add(pubUtils.lnkAnonymousInfo());
        links.add(pubUtils.lnkAnonymousTokens());
        links.add(pubUtils.lnkAdmin());
        links.add(pubUtils.lnkMe());

        PubItem pubItem = new PubItem(HttpStatusCode.OK, links);
        return pubUtils.toResponse(pubItem).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils);
    }

    @Path($me)
    public MeResource getMeResource() {
        return new MeResource(executionManager, pubUtils);
    }

    @Path($identities)
    public IdentitiesResource getUsersResource() {
        return new IdentitiesResource(executionManager, pubUtils);
    }

    @Path($policies)
    public PoliciesResource getPoliciesResource() {
        return new PoliciesResource(executionManager, pubUtils);
    }

    @Path($anonymous)
    public AnonymousResource getAnonymousResource() {
        return new AnonymousResource(executionManager, pubUtils);
    }
}

