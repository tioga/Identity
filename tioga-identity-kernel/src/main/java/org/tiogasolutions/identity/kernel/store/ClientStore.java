package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.identity.kernel.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        ClientEo client = ClientEo.create("admin", "password-123");

        RealmEo defaultRealm = client.createSystem("default").createRealm("default");
        RoleEo adminRole = defaultRealm.createRole("admin");

        client.createUser("me@jacobparr.com", "password-123").assign(adminRole);
        client.createUser("harlan.work@gmail.com", "password-123").assign(adminRole);

        this.clients.add(client);
    }

    private void createSpending() {
        ClientEo client = ClientEo.create("spending-fyi", "password-123");

        // Spending-FYI has only one system and one realm
        RealmEo defaultRealm = client.createSystem("default").createRealm("default");
        // We have two roles, admin and user.
        RoleEo adminRole = defaultRealm.createRole("admin");
        RoleEo userRole = defaultRealm.createRole("user");

        // Admin users
        client.createUser("me@jacobparr.com", "password-123").assign(adminRole, userRole);

        // Regular users
        client.createUser("angieparr@gmail.com", "password-123").assign(userRole);
        client.createUser("tigerspanda1994@gmail.com", "password-123").assign(userRole);
        client.createUser("jedijes@gmail.com", "password-123").assign(userRole);

        this.clients.add(client);
    }

    private void createTioga() {
        // The "tioga" client is possibly the most complex. This client brings
        // together Tioga Solution's various micro-services under one roof. The
        // idea being that one username/password can be used to use all these APIs
        ClientEo client = ClientEo.create("tioga", "password-123");

        // Create the users of this client
        UserEo jacob = client.createUser("me@jacobparr.com", "password-123");
        UserEo harlan = client.createUser("harlan.work@gmail.com", "password-123");
        UserEo chris = client.createUser("chrisjasp@gmail.com", "password-123");

        // We have one system for each micro-service
        List<SystemEo> systems = Arrays.asList(
                client.createSystem("notify"),
                client.createSystem("push"),
                client.createSystem("identity"),
                client.createSystem("ack-im")
        );

        // Create the "admin" realm and admin roles for each system
        for (SystemEo system : systems) {
            RoleEo role = system.createRealm("admin").createRole("admin");
            // Jacob and Harlan get admin rights
            jacob.assign(role);
            harlan.assign(role);
        }

        // The "test" realm is a real realm/domain/space used strictly for testing
        for (SystemEo system : systems) {
            RoleEo role = system.createRealm("test").createRole("user");
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
        UserEo jacob = client.createUser("me@jacobparr.com", "password-123");
        UserEo harlan = client.createUser("harlan.work@gmail.com", "password-123");
        UserEo rich = client.createUser("rich@westcoastimaging.com", "password-123");
        UserEo angie = client.createUser("angieparr@gmail.com", "password-123");
        UserEo brit = client.createUser("tigerspanda1994@gmail.com", "password-123");
        UserEo jesse = client.createUser("jedijes@gmail.com", "password-123");
        UserEo joe = client.createUser("joseph2jsh@gmail.com", "password-123");
        UserEo hannah = client.createUser("hn.noon@gmail.com", "password-123");

        // Photo Lab is not multi-tenant, so we create the one "default" realm.
        RealmEo glacierRealm = client.createSystem("glacier").createRealm("default");
        RealmEo tenayaRealm = client.createSystem("tenaya").createRealm("default");
        RealmEo basecampRealm = client.createSystem("basecamp").createRealm("default");

        // Assign all the admin roles
        for (RealmEo realm : Arrays.asList(glacierRealm, tenayaRealm, basecampRealm)) {
            RoleEo role = realm.createRole("admin");
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
            if (EqualsUtils.objectsEqual(name, client.getName())) {
                return client;
            }
        }
        return null;
    }

    public ClientEo findByToken(String token) {
        for (ClientEo client : clients) {
            if (EqualsUtils.objectsEqual(token, client.getAuthorizationToken())) {
                return client;
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
