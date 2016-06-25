package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.identity.pub.tenant.PubTenants;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;

public class TenantsResource {

    private final TenantStore tenantStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<TenantEo> executionManager;

    public TenantsResource(ExecutionManager<TenantEo> executionManager, TenantStore tenantStore, IdentityPubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    @GET
    @RolesAllowed($ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTenants(@QueryParam("offset") String offset,
                               @QueryParam("limit") String limit,
                               @QueryParam("include") List<String> includes) {
        List<TenantEo> tenants = tenantStore.getAll();
        PubTenants pubTenants = pubUtils.toTenants(HttpStatusCode.OK, tenants, includes, offset, limit);
        return pubUtils.toResponse(pubTenants).build();
    }

    @Path("{tenantName}")
    public TenantResource getTenantResource(@PathParam("tenantName") String tenantName) {
        return new TenantResource(executionManager, tenantStore, pubUtils, tenantName);
    }
}
