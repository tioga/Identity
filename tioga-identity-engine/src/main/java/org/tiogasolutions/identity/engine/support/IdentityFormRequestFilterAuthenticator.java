package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.RequestFilterAuthenticator;
import org.tiogasolutions.app.standard.session.Session;
import org.tiogasolutions.app.standard.session.SessionStore;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.engine.mock.Account;
import org.tiogasolutions.identity.engine.mock.IdentityAuthenticationResponseFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class IdentityFormRequestFilterAuthenticator implements RequestFilterAuthenticator {

    private final SessionStore sessionStore;
    private final IdentityAuthenticationResponseFactory authenticationResponseFactory;

    public IdentityFormRequestFilterAuthenticator(IdentityAuthenticationResponseFactory authenticationResponseFactory, SessionStore sessionStore) {
        this.sessionStore = sessionStore;
        this.authenticationResponseFactory = authenticationResponseFactory;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.FORM_AUTH;
    }

    @Override
    public SecurityContext authenticate(ContainerRequestContext requestContext) {
        Session session = sessionStore.getSession(requestContext);
        if (session == null) throw ApiException.unauthorized("No session");

        Account account = (Account)session.get("account");
        if (account == null) throw ApiException.unauthorized("No account");

        return new SessionBasedSecurityContext(requestContext.getSecurityContext(), account);
    }

    private static class SessionBasedSecurityContext implements SecurityContext {
        private final boolean secure;
        private final Account account;
        public SessionBasedSecurityContext(SecurityContext securityContext, Account account) {
            this.account = account;
            this.secure = securityContext.isSecure();
        }
        @Override public boolean isUserInRole(String role) {
            return false;
        }
        @Override public boolean isSecure() {
            return secure;
        }
        @Override public String getAuthenticationScheme() {
            return SecurityContext.FORM_AUTH;
        }
        @Override public Principal getUserPrincipal() {
            return account::getEmail;
        }
    }
}
