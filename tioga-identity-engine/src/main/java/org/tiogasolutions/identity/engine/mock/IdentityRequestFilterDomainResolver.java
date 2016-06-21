package org.tiogasolutions.identity.engine.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.app.standard.jaxrs.filters.StandardRequestFilterDomainResolver;
import org.tiogasolutions.app.standard.session.Session;
import org.tiogasolutions.app.standard.session.SessionStore;

import javax.ws.rs.container.ContainerRequestContext;

public class IdentityRequestFilterDomainResolver implements StandardRequestFilterDomainResolver {

    private final AccountStore accountStore;
    private final SessionStore sessionStore;

    @Autowired
    public IdentityRequestFilterDomainResolver(AccountStore accountStore, SessionStore sessionStore) {
        this.sessionStore = sessionStore;
        this.accountStore = accountStore;
    }

    @Override
    public Object getDomain(ContainerRequestContext requestContext) {
        Session session = sessionStore.getSession(requestContext);
        if (session == null) return null;
        return session.get("account");
    }

    @Override
    public String getDomainName(ContainerRequestContext requestContext) {
        Account account = (Account)getDomain(requestContext);
        return (account == null) ? null : account.getEmail();
    }
}
