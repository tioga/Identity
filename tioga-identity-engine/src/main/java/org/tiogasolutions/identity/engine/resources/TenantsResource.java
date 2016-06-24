package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class TenantsResource {

    private final TenantStore tenantStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<TenantEo> executionManager;

    public TenantsResource(ExecutionManager<TenantEo> executionManager, TenantStore tenantStore, IdentityPubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    @Path("{tenantName}")
    public TenantResource getTenantResource(@PathParam("tenantName") String tenantName) {
        return new TenantResource(executionManager, tenantStore, pubUtils, tenantName);
    }
}
