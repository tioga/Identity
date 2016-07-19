package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.TokenRequestFilterAuthenticator;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;
import static org.tiogasolutions.identity.kernel.constants.Roles.$USER;
import static org.tiogasolutions.identity.kernel.domain.DomainProfileEo.INTERNAL_DOMAIN;

public class IdentityTokenRequestFilterAuthenticator extends TokenRequestFilterAuthenticator {

    private final IdentityStore identityStore;
    private final DomainStore domainStore;

    public IdentityTokenRequestFilterAuthenticator(DomainStore domainStore, IdentityStore identityStore) {
        this.identityStore = identityStore;
        this.domainStore = domainStore;
    }

    @Override
    protected SecurityContext validate(ContainerRequestContext requestContext, String token) {
        try {

            DomainProfileEo domainProfile = domainStore.findByToken(token);
            if (domainProfile == null) {
                throw ApiException.unauthorized("Invalid access token");
            }

            List<String> roles = new ArrayList<>();
            roles.add($USER); // Everyone is a user

            if (INTERNAL_DOMAIN.equalsIgnoreCase(domainProfile.getDomainName())) {
                // If this is the "internal" profile then you get the admin role
                roles.add($ADMIN);
            }

            IdentityKernel identityKernel = new IdentityKernel(domainStore, identityStore, domainProfile);

            return new IdentitySecurityContext(requestContext.getSecurityContext(), identityKernel, roles);

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid access token");
        }
    }

}
