package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdminResource {

    private final ExecutionManager<TenantEo> executionManager;
    private final IdentityPubUtils pubUtils;
    private final TenantStore tenantStore;

    public AdminResource(ExecutionManager<TenantEo> executionManager, IdentityPubUtils pubUtils, TenantStore tenantStore) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.uriAdmin());
        pubLinks.add("api", pubUtils.uriApi());
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

}
