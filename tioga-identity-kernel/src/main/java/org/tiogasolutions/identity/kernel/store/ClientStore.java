package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.identity.kernel.domain.*;

import java.util.*;

import static org.tiogasolutions.identity.kernel.constants.Roles.$ADMIN_CLIENT;

public class ClientStore {

    private final List<ClientEo> clients = new ArrayList<>();

    public ClientStore() {
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

        client.addUser("me@jacobparr.com", "password-123").assign(adminRole);
        client.addUser("harlan.work@gmail.com", "password-123").assign(adminRole);

        this.clients.add(client);
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
        client.addUser("me@jacobparr.com", "password-123").assign(adminRole, userRole);

        // Regular users
        client.addUser("angieparr@gmail.com", "password-123").assign(userRole);
        client.addUser("tigerspanda1994@gmail.com", "password-123").assign(userRole);
        client.addUser("jedijes@gmail.com", "password-123").assign(userRole);

        this.clients.add(client);
    }

    private void createTioga() {
        // The "tioga" client is possibly the most complex. This client brings
        // together Tioga Solution's various micro-services under one roof. The
        // idea being that one username/password can be used to use all these APIs
        ClientEo client = ClientEo.create("tioga", "password-123");

        // Create the users of this client
        UserEo jacob = client.addUser("me@jacobparr.com", "password-123");
        UserEo harlan = client.addUser("harlan.work@gmail.com", "password-123");
        UserEo chris = client.addUser("chrisjasp@gmail.com", "password-123");

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
        UserEo jacob = client.addUser("me@jacobparr.com", "password-123");
        UserEo harlan = client.addUser("harlan.work@gmail.com", "password-123");
        UserEo rich = client.addUser("rich@westcoastimaging.com", "password-123");
        UserEo angie = client.addUser("angieparr@gmail.com", "password-123");
        UserEo brit = client.addUser("tigerspanda1994@gmail.com", "password-123");
        UserEo jesse = client.addUser("jedijes@gmail.com", "password-123");
        UserEo joe = client.addUser("joseph2jsh@gmail.com", "password-123");
        UserEo hannah = client.addUser("hn.noon@gmail.com", "password-123");

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

    public ClientEo findByName(String name) {
        for (ClientEo client : clients) {
            if (EqualsUtils.objectsEqual(name, client.getClientName())) {
                return client;
            }
        }
        return null;
    }

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

    public void update(ClientEo clientEo) {
    }

    public List<ClientEo> getAll() {
        return Collections.unmodifiableList(clients);
    }
}
