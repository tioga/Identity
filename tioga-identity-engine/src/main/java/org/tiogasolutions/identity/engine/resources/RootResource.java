/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.identity.engine.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.domain.IdentityInfo;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.tiogasolutions.identity.kernel.constants.Paths.$api;
import static org.tiogasolutions.identity.kernel.constants.Paths.$api_v1;

@Path("/")
@Component
@Scope(value = "prototype")
public class RootResource extends RootResourceSupport {

    private static final Log log = LogFactory.getLog(RootResource.class);
    private static final Long startedAt = System.currentTimeMillis();

    private PubUtils pubUtils;

    @Context
    private UriInfo uriInfo;

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private ExecutionManager<IdentityKernel> executionManager;

    @Autowired
    private DomainStore domainStore;

    @Autowired
    private IdentityStore identityStore;

    public RootResource() {
        log.info("Created ");
    }

    @Override
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() throws Exception {

        PubLinks links = new PubLinks();
        links.add("self", getPubUtils().uriRoot());
        links.add("api", getPubUtils().uriApi());
        links.add("authenticate", getPubUtils().uriAuthenticate());
        links.add("me", getPubUtils().uriMe());
        links.add("admin", getPubUtils().uriAdmin());

        long elapsed = System.currentTimeMillis() - startedAt;
        IdentityInfo identityInfo = new IdentityInfo(HttpStatusCode.OK, links, elapsed);

        return getPubUtils().toResponse(identityInfo).build();
    }

    @GET
    @Path($api)
    public Response getApi() throws Exception {
        URI location = URI.create(getPubUtils().uriApi());
        return Response.seeOther(location).build();
    }

    @Path($api_v1)
    public ApiResource getApiV1() throws Exception {
        return new ApiResource(executionManager, getPubUtils(), domainStore, identityStore);
    }

    private PubUtils getPubUtils() {
        if (pubUtils == null) {
            pubUtils = new PubUtils(uriInfo);
        }
        return pubUtils;
    }
}

