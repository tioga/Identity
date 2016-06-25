package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterDomainResolver;
import org.tiogasolutions.identity.kernel.domain.ClientEo;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class IdentityRequestFilterDomainResolver implements StandardRequestFilterDomainResolver<ClientEo> {

    public IdentityRequestFilterDomainResolver() {
    }

    @Override
    public ClientEo getDomain(ContainerRequestContext rc) {
        SecurityContext sc = rc.getSecurityContext();
        if (sc instanceof IdentityTokenBasedSecurityContext) {
            IdentityTokenBasedSecurityContext isc = (IdentityTokenBasedSecurityContext)sc;
            return isc.getClientEo();
        }
        return null;
    }

    @Override
    public String getDomainName(ContainerRequestContext requestContext) {
        ClientEo profile = getDomain(requestContext);
        return (profile == null) ? null : profile.getName();
    }
}
