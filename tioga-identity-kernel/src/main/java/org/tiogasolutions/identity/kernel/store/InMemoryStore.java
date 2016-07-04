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
import static org.tiogasolutions.identity.kernel.domain.DomainProfileEo.INTERNAL_DOMAIN;

public class InMemoryStore implements DomainStore, UserStore {

    private final List<DomainProfileEo> domainProfiles = new ArrayList<>();
    private final List<UserEo> users = new ArrayList<>();

    public InMemoryStore() {
        createDefault();
        createPhotoLab();
        createTioga();
        createSpending();
    }

    private void createDefault() {

        // Within "Identity", the "admin" domain is used to
        // administer this application and is hard coded into the filter.
        DomainProfileEo domainProfile = DomainProfileEo.create(INTERNAL_DOMAIN, "password-123");

        RealmEo defaultRealm = domainProfile.addSystem("system").addRealm("realm");
        RoleEo adminRole = defaultRealm.createRole("administrator");

        addUser(domainProfile, "me@jacobparr.com", "password-123").assign(adminRole);
        addUser(domainProfile, "harlan.work@gmail.com", "password-123").assign(adminRole);

        this.domainProfiles.add(domainProfile);
    }

    private UserEo addUser(DomainProfileEo domainProfile, String username, String password) {
        UserEo user = UserEo.create(domainProfile, username, password);
        users.add(user);
        return user;
    }

    private void createSpending() {
        DomainProfileEo domainProfile = DomainProfileEo.create("spending-fyi", "password-123");

        // Spending-FYI has only one system and one realm
        RealmEo defaultRealm = domainProfile.addSystem("system").addRealm("realm");
        // We have two roles, admin and user.
        RoleEo adminRole = defaultRealm.createRole("administrator");
        adminRole.addPermission("delete");

        RoleEo userRole = defaultRealm.createRole("user");
        userRole.addPermission("edit");

        // Admin users
        addUser(domainProfile, "me@jacobparr.com", "password-123").assign(adminRole, userRole);

        // Regular users
        addUser(domainProfile, "angieparr@gmail.com", "password-123").assign(userRole);
        addUser(domainProfile, "tigerspanda1994@gmail.com", "password-123").assign(userRole);
        addUser(domainProfile, "jedijes@gmail.com", "password-123").assign(userRole);

        this.domainProfiles.add(domainProfile);
    }

    private void createTioga() {
        // The "tioga" domain profile is possibly the most complex. This profile brings
        // together Tioga Solution's various micro-services under one roof. The
        // idea being that one username/password can be used to use all these APIs
        DomainProfileEo domainProfile = DomainProfileEo.create("tioga", "password-123");

        // Create the users of this profile
        UserEo jacob = addUser(domainProfile, "me@jacobparr.com", "password-123");
        UserEo harlan = addUser(domainProfile, "harlan.work@gmail.com", "password-123");
        UserEo chris = addUser(domainProfile, "chrisjasp@gmail.com", "password-123");

        // We have one system for each micro-service
        List<SystemEo> systems = Arrays.asList(
                domainProfile.addSystem("notify"),
                domainProfile.addSystem("push"),
                domainProfile.addSystem("identity"),
                domainProfile.addSystem("ack-im")
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

        this.domainProfiles.add(domainProfile);
    }

    private void createPhotoLab() {
        DomainProfileEo domainProfile = DomainProfileEo.create("photo-lab", "password-123");

        // Create the users of this profile
        UserEo jacob = addUser(domainProfile, "me@jacobparr.com", "password-123");
        UserEo harlan = addUser(domainProfile, "harlan.work@gmail.com", "password-123");
        UserEo rich = addUser(domainProfile, "rich@westcoastimaging.com", "password-123");
        UserEo angie = addUser(domainProfile, "angieparr@gmail.com", "password-123");
        UserEo brit = addUser(domainProfile, "tigerspanda1994@gmail.com", "password-123");
        UserEo jesse = addUser(domainProfile, "jedijes@gmail.com", "password-123");
        UserEo joe = addUser(domainProfile, "joseph2jsh@gmail.com", "password-123");
        UserEo hannah = addUser(domainProfile, "hn.noon@gmail.com", "password-123");

        // Photo Lab is not multi-tenant, so we addRealm the one "default" realm.
        RealmEo glacierRealm = domainProfile.addSystem("glacier").addRealm("realm");
        RealmEo tenayaRealm = domainProfile.addSystem("tenaya").addRealm("realm");
        RealmEo basecampRealm = domainProfile.addSystem("basecamp").addRealm("realm");

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

        this.domainProfiles.add(domainProfile);
    }

    @Override
    public DomainProfileEo findByName(String name) {
        for (DomainProfileEo domainProfile : domainProfiles) {
            if (EqualsUtils.objectsEqual(name, domainProfile.getDomainName())) {
                return domainProfile;
            }
        }
        return null;
    }

    @Override
    public DomainProfileEo findByToken(String test) {
        for (DomainProfileEo domainProfile : domainProfiles) {
            for (String token : domainProfile.getAuthorizationTokens().values()) {
                if (EqualsUtils.objectsEqual(token, test)) {
                    return domainProfile;
                }
            }
        }
        return null;
    }

    @Override
    public void update(DomainProfileEo domainProfile) {
    }

    @Override
    public List<DomainProfileEo> getAll() {
        return Collections.unmodifiableList(domainProfiles);
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
