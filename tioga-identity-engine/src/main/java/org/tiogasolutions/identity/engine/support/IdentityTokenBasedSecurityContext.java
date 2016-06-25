package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.identity.kernel.domain.TenantEo;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class IdentityTokenBasedSecurityContext implements SecurityContext {

    private final boolean secure;
    private final TenantEo tenantEo;
    private final List<String> roles;

    public IdentityTokenBasedSecurityContext(SecurityContext securityContext, TenantEo tenantEo, List<String> roles) {
        this.tenantEo = tenantEo;
        this.secure = securityContext.isSecure();
        this.roles = (roles == null ? emptyList() : unmodifiableList(roles));
    }

    public TenantEo getTenantEo() {
        return tenantEo;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
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
