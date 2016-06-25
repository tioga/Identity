package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.identity.kernel.domain.*;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class TenantStore {

    private final List<TenantEo> tenants = new ArrayList<>();

    public TenantStore() {
        createDefault();
        createPhotoLab();
        createTioga();
        createSpending();
    }

    private void createDefault() {

        // Within "Identity", the "admin" tenant is used to
        // administer this application and is hard coded into the filter.
        TenantEo tenant = TenantEo.create("admin", "password-123");

        RealmEo defaultRealm = tenant.createSystem("default").createRealm("default");
        RoleEo adminRole = defaultRealm.createRole("admin");

        tenant.createUser("me@jacobparr.com", "password-123").assign(adminRole);
        tenant.createUser("harlan.work@gmail.com", "password-123").assign(adminRole);

        this.tenants.add(tenant);
    }

    private void createSpending() {
        TenantEo tenant = TenantEo.create("spending-fyi", "password-123");

        // Spending-FYI has only one system and one realm
        RealmEo defaultRealm = tenant.createSystem("default").createRealm("default");
        // We have two roles, admin and user.
        RoleEo adminRole = defaultRealm.createRole("admin");
        RoleEo userRole = defaultRealm.createRole("user");

        // Admin users
        tenant.createUser("me@jacobparr.com", "password-123").assign(adminRole, userRole);

        // Regular users
        tenant.createUser("angieparr@gmail.com", "password-123").assign(userRole);
        tenant.createUser("tigerspanda1994@gmail.com", "password-123").assign(userRole);
        tenant.createUser("jedijes@gmail.com", "password-123").assign(userRole);

        this.tenants.add(tenant);
    }

    private void createTioga() {
        // The "tioga" tenant is possibly the most complex. This tenant brings
        // together Tioga Solution's various micro-services under one roof. The
        // idea being that one username/password can be used to use all these APIs
        TenantEo tenant = TenantEo.create("tioga", "password-123");

        // Create the users of this tenant
        UserEo jacob = tenant.createUser("me@jacobparr.com", "password-123");
        UserEo harlan = tenant.createUser("harlan.work@gmail.com", "password-123");
        UserEo chris = tenant.createUser("chrisjasp@gmail.com", "password-123");

        // We have one system for each micro-service
        List<SystemEo> systems = Arrays.asList(
                tenant.createSystem("notify"),
                tenant.createSystem("push"),
                tenant.createSystem("identity"),
                tenant.createSystem("ack-im")
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

        this.tenants.add(tenant);
    }

    private void createPhotoLab() {
        TenantEo tenant = TenantEo.create("photo-lab", "password-123");

        // Create the users of this tenant
        UserEo jacob = tenant.createUser("me@jacobparr.com", "password-123");
        UserEo harlan = tenant.createUser("harlan.work@gmail.com", "password-123");
        UserEo rich = tenant.createUser("rich@westcoastimaging.com", "password-123");
        UserEo angie = tenant.createUser("angieparr@gmail.com", "password-123");
        UserEo brit = tenant.createUser("tigerspanda1994@gmail.com", "password-123");
        UserEo jesse = tenant.createUser("jedijes@gmail.com", "password-123");
        UserEo joe = tenant.createUser("joseph2jsh@gmail.com", "password-123");
        UserEo hannah = tenant.createUser("hn.noon@gmail.com", "password-123");

        // Photo Lab is not multi-tenant, so we create the one "default" realm.
        RealmEo glacierRealm = tenant.createSystem("glacier").createRealm("default");
        RealmEo tenayaRealm = tenant.createSystem("tenaya").createRealm("default");
        RealmEo basecampRealm = tenant.createSystem("basecamp").createRealm("default");

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

        this.tenants.add(tenant);
    }

    public TenantEo findByName(String name) {
        for (TenantEo tenant : tenants) {
            if (EqualsUtils.objectsEqual(name, tenant.getName())) {
                return tenant;
            }
        }
        return null;
    }

    public TenantEo findByToken(String token) {
        for (TenantEo tenant : tenants) {
            if (EqualsUtils.objectsEqual(token, tenant.getAuthorizationToken())) {
                return tenant;
            }
        }
        return null;
    }

    public void update(TenantEo tenantEo) {
    }

    public List<TenantEo> getAll() {
        return Collections.unmodifiableList(tenants);
    }
}
