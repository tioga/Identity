package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.IdentityPubUtils;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;
import org.tiogasolutions.identity.pub.tenant.PubTenant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsNotEqual;
import static org.tiogasolutions.identity.kernel.constants.Paths.$authenticate;
import static org.tiogasolutions.identity.kernel.constants.Paths.$systems;
import static org.tiogasolutions.identity.kernel.constants.Paths.$users;

public class TenantResource {

    private final String tenantName;
    private final TenantStore tenantStore;
    private final IdentityPubUtils pubUtils;
    private final ExecutionManager<TenantEo> executionManager;

    public TenantResource(ExecutionManager<TenantEo> executionManager, TenantStore tenantStore, IdentityPubUtils pubUtils, String tenantName) {
        this.pubUtils = pubUtils;
        this.tenantName = tenantName;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    private TenantEo getTenant() {
        return executionManager.getContext().getDomain();
    }

    @POST
    @Path($authenticate)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCreateToken(@FormParam("password") String password) {

        TenantEo tenantEo = tenantStore.findByName(tenantName);
        if (tenantEo == null || objectsNotEqual(password, tenantEo.getPassword())) {
            throw ApiException.unauthorized("Invalid username or password.");
        }

        tenantEo.generateAccessToken();
        tenantStore.update(tenantEo);

        PubTenant pubTenant = pubUtils.toTenant(HttpStatusCode.OK, tenantEo);
        return pubUtils.toResponse(pubTenant).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTenant(@QueryParam("include") List<String> includes) {
        PubTenant pubTenant = pubUtils.toTenant(HttpStatusCode.OK, getTenant());
        return pubUtils.toResponse(pubTenant).build();
    }

    @Path($users)
    public UsersResource getUsersResource() {
        return new UsersResource(executionManager, pubUtils);
    }

    @Path($systems)
    public SystemsResource getSystemsResource() {
        return new SystemsResource(executionManager, pubUtils);
    }

}
