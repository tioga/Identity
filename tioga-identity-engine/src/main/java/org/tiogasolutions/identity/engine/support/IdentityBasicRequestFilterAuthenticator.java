package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.BasicRequestFilterAuthenticator;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class IdentityBasicRequestFilterAuthenticator extends BasicRequestFilterAuthenticator {

    private final Map<String,String> trusted = new HashMap<>();

    public IdentityBasicRequestFilterAuthenticator(String firstTrusted, String...otherTrusted) {
        this.trusted.putAll(BeanUtils.toMap(firstTrusted));
        this.trusted.putAll(BeanUtils.toMap(otherTrusted));
    }

    @Override
    protected SecurityContext validate(ContainerRequestContext requestContext, String username, String password) {

        if (trusted.containsKey(username) == false) {
            throw ApiException.unauthorized("Invalid username");

        } else if (EqualsUtils.objectsNotEqual(trusted.get(username), password)) {
            throw ApiException.unauthorized("Invalid password");
        }

        return new ApiBasedSecurityContext(requestContext.getSecurityContext(), username);
    }

    private static class ApiBasedSecurityContext implements SecurityContext {
        private final boolean secure;
        private final String username;
        public ApiBasedSecurityContext(SecurityContext securityContext, String username) {
            this.username = username;
            this.secure = securityContext.isSecure();
        }
        @Override public boolean isUserInRole(String role) {
            return false;
        }
        @Override public boolean isSecure() {
            return secure;
        }
        @Override public String getAuthenticationScheme() { return SecurityContext.BASIC_AUTH; }
        @Override public Principal getUserPrincipal() {
            return new Principal() {
                @Override public String getName() { return username; }
            };
        }
    }
}
