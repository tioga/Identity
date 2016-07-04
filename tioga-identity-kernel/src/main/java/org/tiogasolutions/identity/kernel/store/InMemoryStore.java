package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;
import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN_CLIENT;

public class InMemoryStore implements ClientStore, UserStore {

    private final List<ClientEo> clients = new ArrayList<>();
    private final List<UserEo> users = new ArrayList<>();

    public InMemoryStore() {
        createDefault();
        createPhotoLab();
        createTioga();
        createSpending();
    }

    private void createDefault() {

        // Within "Identity", the "admin" client is used to
        // administer this application and is hard coded into the filter.
        ClientEo client = ClientEo.create($ADMIN_CLIENT, "password-123");

        RealmEo defaultRealm = client.addSystem("system").addRealm("realm");
        RoleEo adminRole = defaultRealm.createRole("administrator");

        addUser(client, "me@jacobparr.com", "password-123").assign(adminRole);
        addUser(client, "harlan.work@gmail.com", "password-123").assign(adminRole);

        this.clients.add(client);
    }

    private UserEo addUser(ClientEo client, String username, String password) {
        UserEo user = UserEo.create(client, username, password);
        users.add(user);
        return user;
    }

    private void createSpending() {
        ClientEo client = ClientEo.create("spending-fyi", "password-123");

        // Spending-FYI has only one system and one realm
        RealmEo defaultRealm = client.addSystem("system").addRealm("realm");
        // We have two roles, admin and user.
        RoleEo adminRole = defaultRealm.createRole("administrator");
        adminRole.addPermission("delete");

        RoleEo userRole = defaultRealm.createRole("user");
        userRole.addPermission("edit");

        // Admin users
        addUser(client, "me@jacobparr.com", "password-123").assign(adminRole, userRole);

        // Regular users
        addUser(client, "angieparr@gmail.com", "password-123").assign(userRole);
        addUser(client, "tigerspanda1994@gmail.com", "password-123").assign(userRole);
        addUser(client, "jedijes@gmail.com", "password-123").assign(userRole);

        this.clients.add(client);
    }

    private void createTioga() {
        // The "tioga" client is possibly the most complex. This client brings
        // together Tioga Solution's various micro-services under one roof. The
        // idea being that one username/password can be used to use all these APIs
        ClientEo client = ClientEo.create("tioga", "password-123");

        // Create the users of this client
        UserEo jacob = addUser(client, "me@jacobparr.com", "password-123");
        UserEo harlan = addUser(client, "harlan.work@gmail.com", "password-123");
        UserEo chris = addUser(client, "chrisjasp@gmail.com", "password-123");

        // We have one system for each micro-service
        List<SystemEo> systems = Arrays.asList(
                client.addSystem("notify"),
                client.addSystem("push"),
                client.addSystem("identity"),
                client.addSystem("ack-im")
        );

        // Create the "admin" realm and admin roles for each system
        for (SystemEo system : systems) {
            RoleEo role = system.addRealm("admin").createRole("administrator");
            // Jacob and Harlan get admin rights
            jacob.assign(role);
            harlan.assign(role);
        }

        // The "test" realm is a real realm/domain/space used strictly for testing
        for (SystemEo system : systems) {
            RoleEo role = system.addRealm("test").createRole("user");
            // Let Chris play with the test system...
            jacob.assign(role);
            harlan.assign(role);
            chris.assign(role);
        }

        this.clients.add(client);
    }

    private void createPhotoLab() {
        ClientEo client = ClientEo.create("photo-lab", "password-123");

        // Create the users of this client
        UserEo jacob = addUser(client, "me@jacobparr.com", "password-123");
        UserEo harlan = addUser(client, "harlan.work@gmail.com", "password-123");
        UserEo rich = addUser(client, "rich@westcoastimaging.com", "password-123");
        UserEo angie = addUser(client, "angieparr@gmail.com", "password-123");
        UserEo brit = addUser(client, "tigerspanda1994@gmail.com", "password-123");
        UserEo jesse = addUser(client, "jedijes@gmail.com", "password-123");
        UserEo joe = addUser(client, "joseph2jsh@gmail.com", "password-123");
        UserEo hannah = addUser(client, "hn.noon@gmail.com", "password-123");

        // Photo Lab is not multi-tenant, so we addRealm the one "default" realm.
        RealmEo glacierRealm = client.addSystem("glacier").addRealm("realm");
        RealmEo tenayaRealm = client.addSystem("tenaya").addRealm("realm");
        RealmEo basecampRealm = client.addSystem("basecamp").addRealm("realm");

        // Assign all the admin roles
        for (RealmEo realm : Arrays.asList(glacierRealm, tenayaRealm, basecampRealm)) {
            RoleEo role = realm.createRole("administrator");
            jacob.assign(role);
            harlan.assign(role);
            rich.assign(role);
        }

        // Assign the photographers in Glacier
        RoleEo photographer = glacierRealm.createRole("photographer");
        for (UserEo user : Arrays.asList(jacob, rich, angie)) {
            user.assign(photographer);
        }

        // Assign the consumer in Tenaya
        RoleEo consumer = tenayaRealm.createRole("consumer");
        for (UserEo user : Arrays.asList(jacob, rich, angie, brit, jesse, joe, hannah)) {
            user.assign(consumer);
        }

        this.clients.add(client);
    }

    @Override
    public ClientEo findByName(String name) {
        for (ClientEo client : clients) {
            if (EqualsUtils.objectsEqual(name, client.getClientName())) {
                return client;
            }
        }
        return null;
    }

    @Override
    public ClientEo findByToken(String test) {
        for (ClientEo client : clients) {
            for (String token : client.getAuthorizationTokens().values()) {
                if (EqualsUtils.objectsEqual(token, test)) {
                    return client;
                }
            }
        }
        return null;
    }

    @Override
    public void update(ClientEo clientEo) {
    }

    @Override
    public List<ClientEo> getAll() {
        return Collections.unmodifiableList(clients);
    }

    @Override
    public void addUser(UserEo user) {
        users.add(user);
    }

    public List<UserEo> getUsers(String username) {
        if (StringUtils.isBlank(username)) {
            return unmodifiableList(users);

        } else {
            List<UserEo> usersList = users.stream().filter(user -> objectsEqual(username, user.getUsername())).collect(toList());
            return unmodifiableList(usersList);
        }
    }

    public UserEo findUserByName(String username) {
        for (UserEo user : users) {
            if (objectsEqual(username, user.getUsername())) {
                return user;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }

    public UserEo findUserById(String id) {
        for (UserEo user : users) {
            if (objectsEqual(id, user.getId())) {
                return user;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }
}
