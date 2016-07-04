package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterDomainResolver;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class IdentityRequestFilterDomainResolver implements StandardRequestFilterDomainResolver<IdentityKernel> {

    public IdentityRequestFilterDomainResolver() {
    }

    @Override
    public IdentityKernel getDomain(ContainerRequestContext rc) {
        SecurityContext sc = rc.getSecurityContext();
        if (sc instanceof IdentityTokenBasedSecurityContext) {
            IdentityTokenBasedSecurityContext isc = (IdentityTokenBasedSecurityContext)sc;
            return isc.getIdentityKernel();
        }
        return null;
    }

    @Override
    public String getDomainName(ContainerRequestContext requestContext) {
        IdentityKernel kernel = getDomain(requestContext);
        return (kernel == null) ? null : kernel.getDomainName();
    }
}
