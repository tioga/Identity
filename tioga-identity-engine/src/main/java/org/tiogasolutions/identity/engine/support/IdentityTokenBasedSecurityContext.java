package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.identity.kernel.IdentityKernel;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class IdentityTokenBasedSecurityContext implements SecurityContext {

    private final boolean secure;
    private final IdentityKernel identityKernel;
    private final List<String> roles;

    public IdentityTokenBasedSecurityContext(SecurityContext securityContext, IdentityKernel identityKernel, List<String> roles) {
        this.identityKernel = identityKernel;
        this.secure = securityContext.isSecure();
        this.roles = (roles == null ? emptyList() : unmodifiableList(roles));
    }

    public IdentityKernel getIdentityKernel() {
        return identityKernel;
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
        return identityKernel::getDomainName;
    }
}
