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

import static java.util.Collections.singletonList;

public class ApiResource {

    private final IdentityPubUtils pubUtils;
    private final TenantStore tenantStore;
    private final ExecutionManager<TenantEo> executionManager;

    public ApiResource(ExecutionManager<TenantEo> executionManager, IdentityPubUtils pubUtils, TenantStore tenantStore) {
        this.pubUtils = pubUtils;
        this.tenantStore = tenantStore;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.getApiUri());
        pubLinks.add("root", pubUtils.getRootUri());
        pubLinks.add("tenant", pubUtils.getTenantUri(null));
        pubLinks.add("tenant-users", pubUtils.getTenantUri(singletonList("users")));
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public AdminResource getAdminResource() {
        return new AdminResource(executionManager, pubUtils, tenantStore);
    }

    @Path("/tenant")
    @Produces(MediaType.APPLICATION_JSON)
    public TenantResource getTenantResource() {
        return new TenantResource(executionManager, pubUtils);
    }
}
