package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterDomainResolver;
import org.tiogasolutions.identity.kernel.IdentityKernel;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class IdentityRequestFilterDomainResolver implements StandardRequestFilterDomainResolver<IdentityKernel> {

    public IdentityRequestFilterDomainResolver() {
    }

    @Override
    public IdentityKernel getDomain(ContainerRequestContext rc) {
        SecurityContext sc = rc.getSecurityContext();
        if (sc instanceof IdentitySecurityContext) {
            IdentitySecurityContext isc = (IdentitySecurityContext)sc;
            return isc.getIdentityKernel();
        }
        return null;
    }

    @Override
    public String getDomainName(ContainerRequestContext requestContext) {
        IdentityKernel kernel = getDomain(requestContext);
        return (kernel == null || kernel.getCurrentDomainProfile() == null) ? null : kernel.getCurrentDomainProfile().getDomainName();
    }
}
