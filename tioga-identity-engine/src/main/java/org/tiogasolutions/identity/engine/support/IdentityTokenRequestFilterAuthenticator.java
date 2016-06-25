package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.TokenRequestFilterAuthenticator;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.kernel.store.TenantStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;
import static org.tiogasolutions.identity.kernel.constants.Roles.$USER;

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

            List<String> roles = new ArrayList<>();
            roles.add($USER); // Everyone is a user

            if ("admin".equalsIgnoreCase(tenantEo.getName())) {
                // If this is the "admin" tenant
                // then you get the admin role
                roles.add($ADMIN);
            }

            return new IdentityTokenBasedSecurityContext(requestContext.getSecurityContext(), tenantEo, roles);

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid access token");
        }
    }

}
