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
        // createTioga();
        createSpending();
    }

    private void createDefault() {

        // Within "Identity", the "admin" domain is used to
        // administer this application and is hard coded into the filter.
        DomainProfileEo domainProfile = DomainProfileEo.create(INTERNAL_DOMAIN, "password-123");

        PolicyEo defaultPolicy = domainProfile.addPolicy("policy");
        RealmEo defaultRealm = defaultPolicy.addRealm("realm");
        RoleEo adminRole = defaultPolicy.addRole("administrator");

        addUser(domainProfile, "me@jacobparr.com", "password-123").assign(defaultRealm, adminRole);
        addUser(domainProfile, "harlan.work@gmail.com", "password-123").assign(defaultRealm, adminRole);

        this.domainProfiles.add(domainProfile);
    }

    private UserEo addUser(DomainProfileEo domainProfile, String username, String password) {
        UserEo user = UserEo.create(domainProfile, username, password);
        users.add(user);
        return user;
    }

    private void createSpending() {
        DomainProfileEo domainProfile = DomainProfileEo.create("spending-fyi", "password-123");

        // Spending-FYI has only one policy and one realm
        PolicyEo defaultPolicy = domainProfile.addPolicy("policy");
        RealmEo defaultRealm = defaultPolicy.addRealm("realm");
        // We have two roles, admin and user.
        RoleEo adminRole = defaultPolicy.addRole("administrator");
        // adminRole.addPermission("delete");

        RoleEo userRole = defaultPolicy.addRole("user");
        // userRole.addPermission("edit");

        // Admin users
        addUser(domainProfile, "me@jacobparr.com", "password-123")
                .assign(defaultRealm, userRole)
                .assign(defaultRealm, adminRole);

        // Regular users
        addUser(domainProfile, "angieparr@gmail.com", "password-123")
                .assign(defaultRealm, userRole);

        addUser(domainProfile, "tigerspanda1994@gmail.com", "password-123")
                .assign(defaultRealm, userRole);

        addUser(domainProfile, "jedijes@gmail.com", "password-123")
                .assign(defaultRealm, userRole);

        this.domainProfiles.add(domainProfile);
    }

//    private void createTioga() {
//        // The "tioga" domain profile is possibly the most complex. This profile brings
//        // together Tioga Solution's various micro-services under one roof. The
//        // idea being that one username/password can be used to use all these APIs
//        DomainProfileEo domainProfile = DomainProfileEo.create("tioga", "password-123");
//
//        // Create the users of this profile
//        UserEo jacob = addUser(domainProfile, "me@jacobparr.com", "password-123");
//        UserEo harlan = addUser(domainProfile, "harlan.work@gmail.com", "password-123");
//
//        UserEo apiGateway = addUser(domainProfile, "api.gateway@tioga.solutions", "password-123");
//
//        UserEo netfile = addUser(domainProfile, "boss@netfile.com", "password-123");
//        UserEo netfileApiClient = addUser(domainProfile, "tioga.client@netfile.com", "password-123");
//
//        List<PolicyEo> policies = Arrays.asList(
//            domainProfile.addPolicy("notify"),
//            domainProfile.addPolicy("push"),
//            domainProfile.addPolicy("identity")
//        );
//
//        for (PolicyEo policy : policies) {
//            // Two realms: /api/v1/admin and /api/v1/client
//            RealmEo adminRealm = policy.addRealm("admin");
//            RealmEo clientRealm = policy.addRealm("client");
//            // The Administrator role is applied within the admin realm.
//            RoleEo adminRole = policy.addRole("administrator");
//            // The API role is used within the client realm to work with the microservice
//            RoleEo apiRole = policy.addRole("api");
//            // The Owner role is used with the client realm to configure/manage the microservice
//            RoleEo ownerRole = policy.addRole("owner");
//
//            // Jacob & Harlan are both admins and owners.
//            jacob.assign(adminRealm, adminRole)
//                 .assign(clientRealm, ownerRole);
//
//            harlan.assign(adminRealm, adminRole)
//                  .assign(clientRealm, ownerRole);
//
//            netfile.assign(clientRealm, ownerRole);
//        }
//
//        this.domainProfiles.add(domainProfile);
//    }

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
        PolicyEo glacierPolicy = domainProfile.addPolicy("glacier");
        PolicyEo tenayaPolicy = domainProfile.addPolicy("tenaya");
        PolicyEo basecampPolicy = domainProfile.addPolicy("basecamp");

//        RealmEo glacierRealm = glacierPolicy.addRealm("realm");
//        RealmEo tenayaRealm = tenayaPolicy.addRealm("realm");
//        RealmEo basecampRealm = basecampPolicy.addRealm("realm");
//
//        // Assign all the admin roles
//        for (RealmEo realm : Arrays.asList(glacierRealm, tenayaRealm, basecampRealm)) {
//            RoleEo role = policy.createRole("administrator");
//            jacob.assign(role);
//            harlan.assign(role);
//            rich.assign(role);
//        }
//
//        // Assign the photographers in Glacier
//        RoleEo photographer = glacierRealm.createRole("photographer");
//        for (UserEo user : Arrays.asList(jacob, rich, angie)) {
//            user.assign(photographer);
//        }
//
//        // Assign the consumer in Tenaya
//        RoleEo consumer = tenayaRealm.createRole("consumer");
//        for (UserEo user : Arrays.asList(jacob, rich, angie, brit, jesse, joe, hannah)) {
//            user.assign(consumer);
//        }
//
//        this.domainProfiles.add(domainProfile);
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
