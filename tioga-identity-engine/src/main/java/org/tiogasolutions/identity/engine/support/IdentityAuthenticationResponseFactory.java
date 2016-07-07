package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.app.standard.jaxrs.auth.StandardAuthenticationResponseFactory;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubStatus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

public class IdentityAuthenticationResponseFactory implements StandardAuthenticationResponseFactory {

    public IdentityAuthenticationResponseFactory() {
    }

    @Override
    public Response createForbiddenResponse(ContainerRequestContext requestContext) {
        String msg = "You do not have permission to access this resource.";
        PubItem pubItem = new PubItem(new PubStatus(HttpStatusCode.FORBIDDEN.getCode(), msg));
        return new PubUtils(requestContext).toResponse(pubItem).build();
    }

    @Override
    public Response createUnauthorizedResponse(ContainerRequestContext requestContext, String authenticationScheme) {
        String msg = "Invalid authorization token.";
        PubItem pubItem = new PubItem(new PubStatus(HttpStatusCode.UNAUTHORIZED.getCode(), msg));
        Response.ResponseBuilder builder = new PubUtils(requestContext).toResponse(pubItem);
        return builder.build();
    }
}
