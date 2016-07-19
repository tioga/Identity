package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.RequestFilterAuthenticator;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import static java.util.Collections.*;

public class UnauthenticatedRequestFilterAuthenticator implements RequestFilterAuthenticator {

    private final IdentityStore identityStore;
    private final DomainStore domainStore;

    public UnauthenticatedRequestFilterAuthenticator(DomainStore domainStore, IdentityStore identityStore) {
        this.identityStore = identityStore;
        this.domainStore = domainStore;
    }

    @Override
    public SecurityContext authenticate(ContainerRequestContext requestContext) {
        IdentityKernel identityKernel = new IdentityKernel(domainStore, identityStore, null);
        return new IdentitySecurityContext(requestContext.getSecurityContext(), identityKernel, singletonList("ANONYMOUS"));
    }

    @Override
    public String getAuthenticationScheme() {
        return "ANONYMOUS";
    }
}
