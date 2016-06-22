package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantProfileEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ApiResource {

    private final IdentityPubUtils pubUtils;
    private final TenantStore tenantStore;
    private final ExecutionManager<TenantProfileEo> executionManager;

    public ApiResource(ExecutionManager<TenantProfileEo> executionManager, IdentityPubUtils pubUtils, TenantStore tenantStore) {
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.getUris().getApi());
        pubLinks.add("root", pubUtils.getUris().getRoot());
        pubLinks.add("client", pubUtils.getUris().getClient());
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, tenantStore);
    }

    @Path("/client")
    @Produces(MediaType.APPLICATION_JSON)
    public ClientResource getClientResource() {
        return new ClientResource(executionManager, pubUtils);
    }
}