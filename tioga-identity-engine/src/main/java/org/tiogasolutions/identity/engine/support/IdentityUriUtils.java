package org.tiogasolutions.identity.engine.support;

import javax.ws.rs.core.UriInfo;

public class IdentityUriUtils {

    private final UriInfo uriInfo;

    public IdentityUriUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public String getRoot() {
        return uriInfo.getBaseUriBuilder().toTemplate();
    }

    public String getApi() {
        return uriInfo.getBaseUriBuilder().path("api").toTemplate();
    }

    public String getAdmin() {
        return uriInfo.getBaseUriBuilder().path("api/admin").toTemplate();
    }

    public String getClient() {
        return uriInfo.getBaseUriBuilder().path("api/client").toTemplate();
    }

    public String getClientUsers() {
        return uriInfo.getBaseUriBuilder().path("api/client/users").toTemplate();
    }

    public String getClientUserById(String id) {
        return uriInfo.getBaseUriBuilder().path("api/client/users/by-id").path(id).toTemplate();
    }

    public String getClientUserByName(String name) {
        return uriInfo.getBaseUriBuilder().path("api/client/users/by-name").path(name).toTemplate();
    }
}
