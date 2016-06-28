package org.tiogasolutions.identity.engine.support;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.kernel.domain.*;
import org.tiogasolutions.identity.pub.client.*;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLink;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.tiogasolutions.identity.kernel.constants.Paths.*;
import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN;
import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN_CLIENT;

public class PubUtils {

    private final UriInfo uriInfo;

    public PubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public PubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public Response.ResponseBuilder toResponse(PubItem pubItem) {
        return Response.status(pubItem.get_status().getCode()).entity(pubItem);
    }






    public PubToken toToken(HttpStatusCode statusCode, ClientEo client, String tokenName) {
        PubLinks links = new PubLinks();
        links.add("self", uriToken());

        links.add("client", uriClient());

        // We cannot check the security context because we may have just authenticated in
        // which case the SC thinks we are anonymous even though this *IS* the admin client.
        if ($ADMIN_CLIENT.equals(client.getClientName())) {
            links.add("admin",   uriAdmin());
            links.add("admin-clients", uriClients(null, null, null));
            links.add("admin-clients-links", uriClients(singletonList("links"), null, null));
        }

        links.add("systems",       uriSystems(null, null, null));
        links.add("systems-links", uriSystems(singletonList("links"), null, null));
        links.add("systems-items", uriSystems(singletonList("items"), null, null));

        links.add("users",       uriUsers(null, null, null, null));
        links.add("users-links", uriUsers(singletonList("links"), null, null, null));
        links.add("users-items", uriUsers(singletonList("items"), null, null, null));

        return new PubToken(
                toStatus(statusCode),
                links,
                tokenName,
                client.getClientName(),
                client.getAuthorizationTokens().get(PubToken.DEFAULT)
        );
    }






    public PubClient toClient(SecurityContext sc, HttpStatusCode statusCode, ClientEo client) {

        PubLinks links = new PubLinks();
        links.add("self", uriClient());

        if (sc != null && sc.isUserInRole($ADMIN)) {
            links.add("admin",   uriAdmin());
            links.add("admin-clients", uriClients(null, null, null));
        }

        links.add("systems",       uriSystems(null, null, null));
        links.add("systems-links", uriSystems(singletonList("links"), null, null));
        links.add("systems-items", uriSystems(singletonList("items"), null, null));

        links.add("users",       uriUsers(null, null, null, null));
        links.add("users-links", uriUsers(singletonList("links"), null, null, null));
        links.add("users-items", uriUsers(singletonList("items"), null, null, null));

        List<PubSystem> pubSystems = new ArrayList<>();
        for (SystemEo system : client.getSystems()) {
            PubSystem pubSystem = toSystem(null, system);
            pubSystems.add(pubSystem);
        }

        return new PubClient(
                toStatus(statusCode),
                links,
                client.getClientName(),
                client.getRevision(),
                client.getStatus(),
                client.getAuthorizationTokens(),
                client.getPassword(),
                client.getDbName(),
                pubSystems);
    }

    public PubClients toClients(SecurityContext sc, HttpStatusCode statusCode, List<ClientEo> clients, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriClients(includes, offset, limit));
        links.add("self-links", uriClients(singletonList("links"), offset, limit));

        links.add("first", uriClients(includes, offset, limit));
        links.add("prev",  uriClients(includes, offset, limit));
        links.add("next",  uriClients(includes, offset, limit));
        links.add("last",  uriClients(includes, offset, limit));

        List<PubLink> linksList = new ArrayList<>();
        for (ClientEo client : clients) {
            linksList.add(new PubLink(client.getClientName(), uriAdminClient(client)));
            linksList.add(new PubLink("impersonate-"+client.getClientName(), uriImpersonate(client)));
        }

        return new PubClients(
                toStatus(statusCode),
                links,
                linksList.size(),
                linksList.size(),
                0,
                999999999,
                includes.contains("links") ? linksList : null);
    }

    public PubSystem toSystem(HttpStatusCode statusCode, SystemEo system) {

        PubLinks links = new PubLinks();
        links.add("self", uriSystemById(system));

        List<PubRealm> realms = new ArrayList<>();
        for (RealmEo realm : system.getRealms()) {
            PubRealm pubRealm = toRealm(null, realm);
            realms.add(pubRealm);
        }

        return new PubSystem(
                toStatus(statusCode),
                links,
                system.getId(),
                system.getSystemName(),
                system.getClient().getClientName(),
                realms
        );
    }

    public PubRealm toRealm(HttpStatusCode statusCode, RealmEo realm) {

        PubLinks links = new PubLinks();
        links.add("self", uriRealmById(realm));

        List<PubRole> roles = new ArrayList<>();
        for (RoleEo role : realm.getRoles()) {
            PubRole pubRole = toRole(null, role);
            roles.add(pubRole);
        }

        return new PubRealm(
                toStatus(statusCode),
                links,
                realm.getId(),
                realm.getRealmName(),
                realm.getSystem().getClient().getClientName(),
                realm.getSystem().getSystemName(),
                roles);
    }

    public PubRole toRole(HttpStatusCode statusCode, RoleEo role) {

        PubLinks links = new PubLinks();
        links.add("self", uriRoleById(role));

        List<PubPermission> permissions = new ArrayList<>();
        for (Permission permission : role.getPermissions()) {
            PubPermission pubPermission = toPermission(null, permission);
            permissions.add(pubPermission);
        }

        return new PubRole(
                toStatus(statusCode),
                links,
                role.getId(),
                role.getRoleName(),
                role.getRealm().getSystem().getClient().getClientName(),
                role.getRealm().getSystem().getSystemName(),
                role.getRealm().getRealmName(),
                permissions);
    }

    private PubPermission toPermission(HttpStatusCode statusCode, Permission permission) {

        return new PubPermission(
                permission.getPermissionName(),
                permission.getRole().getRealm().getSystem().getClient().getClientName(),
                permission.getRole().getRealm().getSystem().getSystemName(),
                permission.getRole().getRealm().getRealmName(),
                permission.getRole().getRoleName());
    }

    public PubSystems toSystems(HttpStatusCode statusCode, ClientEo client, List<String> includes, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();
        List<SystemEo> systems = client.getSystems();

        links.add("self",       uriSystems(includes, offset, limit));
        links.add("self-items", uriSystems(singletonList("items"), offset, limit));
        links.add("self-links", uriSystems(singletonList("links"), offset, limit));


        if (systems.size() > 0) {
            SystemEo first = systems.get(0);
            links.add("first-system", uriSystemById(first));
        }

        links.add("first", uriSystems(null, 0, limit));
        links.add("prev",  uriSystems(null, 0, limit));
        links.add("next",  uriSystems(null, 0, limit));
        links.add("last",  uriSystems(null, 0, limit));

        List<PubSystem> itemsList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < systems.size(); i++) {
            SystemEo system = systems.get(i);
            PubSystem pubSystem = toSystem(null, system);
            itemsList.add(pubSystem);
            linksList.add(pubSystem.get_links().get("self").clone(pubSystem.getSystemName()));
        }

        return new PubSystems(
                toStatus(statusCode),
                links,
                itemsList.size(),
                itemsList.size(),
                0,
                999999999,
                includes.contains("items") ? itemsList : null,
                includes.contains("links") ? linksList : null);
    }





    public PubUser toUser(HttpStatusCode statusCode, UserEo user) {

        PubLinks links = new PubLinks();
        links.add("self", uriUserById(user));

        return new PubUser(
                toStatus(statusCode),
                links,
                user.getId(),
                user.getRevision(),
                user.getUsername(),
                user.getPassword(),
                user.getClient().getClientName(),
                user.getAssignedRoles()
        );
    }

    public PubUsers toUsers(HttpStatusCode statusCode, List<UserEo> users, List<String> includes, String username, Object offset, Object limit) {
        if (includes == null) includes = emptyList();

        PubLinks links = new PubLinks();

        links.add("self",       uriUsers(includes, username, offset, limit));
        links.add("self-items", uriUsers(singletonList("items"), username, offset, limit));
        links.add("self-links", uriUsers(singletonList("links"), username, offset, limit));

        links.add("user",   uriUserById(null));
        links.add("api",    uriApi());

        if (users.size() > 0) {
            UserEo first = users.get(0);
            links.add("first-user", uriUserById(first));
        }

        links.add("first", uriUsers(null, username, 0, limit));
        links.add("prev",  uriUsers(null, username, 0, limit));
        links.add("next",  uriUsers(null, username, 0, limit));
        links.add("last",  uriUsers(null, username, 0, limit));

        List<PubUser> usersList = new ArrayList<>();
        List<PubLink> linksList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserEo user = users.get(i);
            PubUser pubUser = toUser(null, user);
            usersList.add(pubUser);

            linksList.add(pubUser.get_links().get("self").clone(pubUser.getUsername()));
        }

        return new PubUsers(
                toStatus(statusCode),
                links,
                usersList.size(),
                usersList.size(),
                0,
                999999999,
                includes.contains("items") ? usersList : null,
                includes.contains("links") ? linksList : null);
    }

    private PubStatus toStatus(HttpStatusCode statusCode) {
        return statusCode == null ? null : new PubStatus(statusCode);
    }

    public String uriRoot() {
        return uriInfo.getBaseUriBuilder().toTemplate();
    }

    public String uriApi() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .toTemplate();
    }

    public String uriAuthenticate() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($authenticate)
                .toTemplate();
    }

    public String uriAdmin() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .toTemplate();
    }

    private String uriAdminClient(ClientEo client) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($clients)
                .path(client.getClientName())
                .toTemplate();
    }

    private String uriImpersonate(ClientEo client) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($clients)
                .path(client.getClientName())
                .path($impersonate)
                .toTemplate();
    }

    public String uriClients(List<String> includes, Object offset, Object limit) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($admin)
                .path($clients);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriToken() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($token)
                .toTemplate();
    }

    public String uriClient() {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .toTemplate();
    }

    public String uriSystemById(SystemEo system) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($systems)
                .path(system.getId())
                .toTemplate();
    }

    public String uriRealmById(RealmEo realm) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($realms)
                .path(realm.getId())
                .toTemplate();
    }

    public String uriRoleById(RoleEo role) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($roles)
                .path(role.getId())
                .toTemplate();
    }

    private String uriSystems(List<String> includes, Object offsetObj, Object limitObj) {
        if (includes == null || includes.isEmpty()) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, PubUsers.DEFAULT_LIMIT, "limit");

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($systems)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUsers(List<String> includes, String username, Object offsetObj, Object limitObj) {
        if (includes == null) includes = emptyList();

        int offset = toInt(offsetObj, 0, "offset");
        int limit = toInt(limitObj, PubUsers.DEFAULT_LIMIT, "limit");
        if (username == null) username = "";

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($client)
                .path($users)
                .queryParam("username", username)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        for (String include : includes) {
            builder.queryParam("include", include);
        }

        return builder.toTemplate();
    }

    public String uriUserById(UserEo user) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v1)
                .path($users)
                .path(user == null ? "{id}" : user.getId())
                .toTemplate();
    }

    private int toInt(Object value, Object defaultValue, String paramName) {
        if (value == null && defaultValue == null) {
            throw ApiException.badRequest(String.format("The parameter %s must be specified.", paramName));

        } else if (value == null) {
            value = defaultValue;
        }

        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            throw ApiException.badRequest(String.format("The parameter %s must be an integral value.", paramName));
        }
    }
}
