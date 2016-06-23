package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterDomainResolver;
import org.tiogasolutions.identity.kernel.domain.TenantEo;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class IdentityRequestFilterDomainResolver implements StandardRequestFilterDomainResolver<TenantEo> {

    public IdentityRequestFilterDomainResolver() {
    }

    @Override
    public TenantEo getDomain(ContainerRequestContext rc) {
        SecurityContext sc = rc.getSecurityContext();
        if (sc instanceof IdentityTokenRequestFilterAuthenticator.TokenBasedSecurityContext) {
            IdentityTokenRequestFilterAuthenticator.TokenBasedSecurityContext isc = (IdentityTokenRequestFilterAuthenticator.TokenBasedSecurityContext)sc;
            return isc.getTenantEo();
        }
        return null;
    }

    @Override
    public String getDomainName(ContainerRequestContext requestContext) {
        TenantEo profile = getDomain(requestContext);
        return (profile == null) ? null : profile.getName();
    }
}
