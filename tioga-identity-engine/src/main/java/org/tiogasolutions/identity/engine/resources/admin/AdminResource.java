package org.tiogasolutions.identity.engine.resources.admin;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.engine.support.PubUtils;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.identity.kernel.constants.Paths.$domains;

public class AdminResource {

    private final ExecutionManager<IdentityKernel> executionManager;
    private final PubUtils pubUtils;
    private final DomainStore domainStore;

    public AdminResource(ExecutionManager<IdentityKernel> executionManager, PubUtils pubUtils, DomainStore domainStore) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
        this.domainStore = domainStore;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {

        PubLinks pubLinks = new PubLinks();
        pubLinks.add("self", pubUtils.uriAdmin());
        pubLinks.add("domains", pubUtils.uriDomains(null, null, null));
        pubLinks.add("api", pubUtils.uriApi());
        PubItem pubItem = new PubItem(HttpStatusCode.OK, pubLinks);

        return pubUtils.toResponse(pubItem).build();
    }

    @Path($domains)
    public DomainsResource getDomainsResource() {
        return new DomainsResource(executionManager, domainStore, pubUtils);
    }
}
