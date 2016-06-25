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
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    private IdentityPubUtils pubUtils;

    @Context
    private UriInfo uriInfo;

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private ExecutionManager<TenantEo> executionManager;

    @Autowired
    private TenantStore tenantStore;

    public RootResource() {
        log.info("Created ");
    }

    @Override
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndex() throws Exception {

        PubLinks links = new PubLinks();
        links.add("self", getPubUtils().uriRoot());
        links.add("api", getPubUtils().uriApi());

        long elapsed = System.currentTimeMillis() - startedAt;
        PubInfo pubInfo = new PubInfo(HttpStatusCode.OK, links, String.format("%s days, %s hours, %s minutes, %s seconds",
                elapsed / (24 * 60 * 60 * 1000),
                elapsed / (60 * 60 * 1000) % 24,
                elapsed / (60 * 1000) % 60,
                elapsed / 1000 % 60));

        return getPubUtils().toResponse(pubInfo).build();
    }

    private static class PubInfo extends PubItem {
        private final String upTime;
        private PubInfo(HttpStatusCode httpStatusCode, PubLinks links, String upTime) {
            super(httpStatusCode, links);
            this.upTime = upTime;
        }
        public String getUpTime() { return upTime; }
    }

    @GET
    @Path($api)
    public Response getApi() throws Exception {
        URI location = URI.create(getPubUtils().uriApi());
        return Response.seeOther(location).build();
    }

    @Path($api_v1)
    public ApiResource getApiV1() throws Exception {
        return new ApiResource(executionManager, getPubUtils(), tenantStore);
    }

    private IdentityPubUtils getPubUtils() {
        if (pubUtils == null) {
            pubUtils = new IdentityPubUtils(uriInfo);
        }
        return pubUtils;
    }
}

