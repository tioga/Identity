package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.TokenRequestFilterAuthenticator;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class IdentityTokenRequestFilterAuthenticator extends TokenRequestFilterAuthenticator {

    private final TenantStore tenantStore;

    public IdentityTokenRequestFilterAuthenticator(TenantStore tenantStore) {
        this.tenantStore = tenantStore;
    }

    @Override
    protected SecurityContext validate(ContainerRequestContext requestContext, String token) {
        try {
            TenantEo tenantEo = tenantStore.findByToken(token);
            if (tenantEo == null) {
                throw ApiException.unauthorized("Invalid access token");
            }
            return new TokenBasedSecurityContext(requestContext.getSecurityContext(), tenantEo);

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid access token");
        }
    }

    public static class TokenBasedSecurityContext implements SecurityContext {
        private final boolean secure;
        private final TenantEo tenantEo;

        public TokenBasedSecurityContext(SecurityContext securityContext, TenantEo tenantEo) {
            this.tenantEo = tenantEo;
            this.secure = securityContext.isSecure();
        }

        public TenantEo getTenantEo() {
            return tenantEo;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "TOKEN";
        }

        @Override
        public Principal getUserPrincipal() {
            return tenantEo::getName;
        }
    }
}
