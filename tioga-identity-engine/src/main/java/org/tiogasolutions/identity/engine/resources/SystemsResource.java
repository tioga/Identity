package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.domain.SystemEo;
import org.tiogasolutions.identity.pub.client.PubSystem;
import org.tiogasolutions.identity.pub.client.PubSystems;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SystemsResource {

    private final PubUtils pubUtils;
    private final ExecutionManager<ClientEo> executionManager;

    public SystemsResource(ExecutionManager<ClientEo> executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    private ClientEo getClient() {
        return executionManager.getContext().getDomain();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("username") String username,
                             @QueryParam("offset") String offset,
                             @QueryParam("limit") String limit,
                             @QueryParam("include") List<String> includes) {

        PubSystems pubSystems = pubUtils.toSystems(HttpStatusCode.OK, getClient(), includes, offset, limit);
        return pubUtils.toResponse(pubSystems).build();
    }

    @GET
    @Path("{systemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("systemId") String systemId) {
        SystemEo system = getClient().findSystemById(systemId);
        if (system == null) {
            throw ApiException.notFound("The specified system was not found.");
        }
        PubSystem pubSystem = pubUtils.toSystem(HttpStatusCode.OK, system);
        return pubUtils.toResponse(pubSystem).build();
    }
}
