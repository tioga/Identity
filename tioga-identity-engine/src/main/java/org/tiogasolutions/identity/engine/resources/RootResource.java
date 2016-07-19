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

    private PubUtils pubUtils;
    private UriInfo uriInfo;

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private ExecutionManager<IdentityKernel> executionManager;

    public RootResource() {
        log.info("Created ");
    }

    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        this.pubUtils = new PubUtils(uriInfo);
    }

    @Override
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @GET
    public Response getRoot() throws Exception {
        return pubUtils.movedPermanently(pubUtils.lnkApiV1());
    }

    @GET
    @Path($api)
    public Response getApi() throws Exception {
        return pubUtils.movedPermanently(pubUtils.lnkApiV1());
    }

    @Path($api_v1)
    public ApiResource getApiV1() throws Exception {
        return new ApiResource(executionManager, pubUtils);
    }
}

