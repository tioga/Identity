package org.tiogasolutions.identity.engine.resources;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.identity.kernel.constants.Paths.$admin;
import static org.tiogasolutions.identity.kernel.constants.Paths.$tenants;

public class ApiResource {

    private final TenantStore tenantStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<TenantEo> executionManager;

    public ApiResource(ExecutionManager<TenantEo> executionManager, IdentityPubUtils pubUtils, TenantStore tenantStore) {
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.uriApi());
        pubLinks.add("status", pubUtils.uriRoot());
        pubLinks.add($tenants, pubUtils.uriTenants(null, null, null));
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

    @Path($admin)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, tenantStore);
    }

    @Path($tenants)
    public TenantsResource getTenantsResource() {
        return new TenantsResource(executionManager, tenantStore, pubUtils);
    }
}
