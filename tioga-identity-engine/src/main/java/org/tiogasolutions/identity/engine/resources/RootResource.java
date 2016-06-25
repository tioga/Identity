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
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.client.PubInfo;
import org.tiogasolutions.identity.pub.client.PubClient;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.$api;
import static org.tiogasolutions.identity.kernel.constants.Paths.$api_v1;
import static org.tiogasolutions.identity.kernel.constants.Paths.$authenticate;

@Path("/")
@Component
@Scope(value = "prototype")
public class RootResource extends RootResourceSupport {

    private static final Log log = LogFactory.getLog(RootResource.class);
    private static final Long startedAt = System.currentTimeMillis();

    private IdentityPubUtils pubUtils;

    @Context
    private UriInfo uriInfo;

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private ExecutionManager<ClientEo> executionManager;

    @Autowired
    private ClientStore clientStore;

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
        links.add("admin", getPubUtils().uriAdmin());
        links.add("authenticate", getPubUtils().uriAuthenticate());
        links.add("client", getPubUtils().uriClient());

        long elapsed = System.currentTimeMillis() - startedAt;
        PubInfo pubInfo = new PubInfo(HttpStatusCode.OK, links, elapsed);

        return getPubUtils().toResponse(pubInfo).build();
    }

    @GET
    @Path($api)
    public Response getApi() throws Exception {
        URI location = URI.create(getPubUtils().uriApi());
        return Response.seeOther(location).build();
    }

    @Path($api_v1)
    public ApiResource getApiV1() throws Exception {
        return new ApiResource(executionManager, getPubUtils(), clientStore);
    }

    private IdentityPubUtils getPubUtils() {
        if (pubUtils == null) {
            pubUtils = new IdentityPubUtils(uriInfo);
        }
        return pubUtils;
    }
}

