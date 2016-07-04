package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.TokenRequestFilterAuthenticator;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.IdentityKernel;
import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.kernel.store.UserStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;
import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN_CLIENT;
import static org.tiogasolutions.identity.kernel.constants.Roles.$USER;

public class IdentityTokenRequestFilterAuthenticator extends TokenRequestFilterAuthenticator {

    private final UserStore userStore;
    private final ClientStore clientStore;

    public IdentityTokenRequestFilterAuthenticator(ClientStore clientStore, UserStore userStore) {
        this.userStore = userStore;
        this.clientStore = clientStore;
    }

    @Override
    protected SecurityContext validate(ContainerRequestContext requestContext, String token) {
        try {

            ClientEo clientEo = clientStore.findByToken(token);
            if (clientEo == null) {
                throw ApiException.unauthorized("Invalid access token");
            }

            List<String> roles = new ArrayList<>();
            roles.add($USER); // Everyone is a user

            if ($ADMIN_CLIENT.equalsIgnoreCase(clientEo.getClientName())) {
                // If this is the "admin" client
                // then you get the admin role
                roles.add($ADMIN);
            }

            IdentityKernel identityKernel = new IdentityKernel(clientStore, userStore, clientEo);

            return new IdentityTokenBasedSecurityContext(requestContext.getSecurityContext(), identityKernel, roles);

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid access token");
        }
    }

}
