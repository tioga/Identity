package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.*;

import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;
import static org.tiogasolutions.identity.kernel.domain.DomainProfileEo.INTERNAL_DOMAIN;

public class InMemoryStore implements DomainStore, IdentityStore {

    private final List<DomainProfileEo> domainProfiles = new ArrayList<>();
    private final List<IdentityEo> users = new ArrayList<>();

    private static final String TIME_AND_BILLING = "time-and-billing";
    private static final String SPENDING_FYI_DOMAIN = "spending-fyi";
    private static final String PHOTO_LAB_DOMAIN = "photo-lab";

    public InMemoryStore() {
        createInternal();
        createPhotoLab();
        createSpending();
        createTimeAndBilling();
    }

    private void createInternal() {

        // Within "Identity", the "admin" domain is used to
        // administer this application and is hard coded into the filter.
        DomainProfileEo domain = DomainProfileEo.create(INTERNAL_DOMAIN);
        PolicyEo policy = domain.addPolicy("default");

        RoleEo ownerRole = policy.addRole("owner");
        RoleEo apiRole = policy.addRole("api");

        RealmEo realm = policy.addRealm(SPENDING_FYI_DOMAIN);
        addUser(domain, "me@jacobparr.com", "password-123").assign(realm, ownerRole);
        addUser(domain, "spending-client", "password-123").assign(realm, apiRole);

        realm = policy.addRealm(TIME_AND_BILLING);
        addUser(domain, "harlan.work@gmail.com", "password-123").assign(realm, ownerRole);
        addUser(domain, "time-and-billing-client", "password-123").assign(realm, apiRole);

        realm = policy.addRealm(PHOTO_LAB_DOMAIN);
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findUserByName(domain, "harlan.work@gmail.com").assign(realm, ownerRole);
        addUser(domain, "photo-lab-client", "password-123").assign(realm, apiRole);

        this.domainProfiles.add(domain);
    }

    private void createSpending() {

        DomainProfileEo domain = DomainProfileEo.create(SPENDING_FYI_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("spending-client", "9876543210");
        domain.setAccessToken("me@jacobparr.com", "9876543210");

        PolicyEo policy = domain.addPolicy("default");

        RoleEo adminRole = policy.addRole("admin");
        RoleEo ownerRole = policy.addRole("owner");
        RoleEo guestRole = policy.addRole("guest");


        // This is my system, I get access to everyting!
        RealmEo globalReal = policy.addRealm("*");
        addUser(domain, "me@jacobparr.com", "password-123").assign(globalReal, adminRole);


        // These are Jacob & Angela's checking & savings accounts
        RealmEo realm = RealmEo.createRealm(policy, "xxxx-2162");
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        addUser(domain, "angieparr@gmail.com", "password-123").assign(realm, ownerRole);

        realm = RealmEo.createRealm(policy, "xxxx-0510");
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findUserByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = RealmEo.createRealm(policy, "xxxx-3100");
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findUserByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = RealmEo.createRealm(policy, "xxxx-1196");
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findUserByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = RealmEo.createRealm(policy, "xxxx-3730");
        findUserByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findUserByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);


        // This is Brittany's checking account
        realm = RealmEo.createRealm(policy, "xxxx-9186");
        addUser(domain, "tigerspanda1994@gmail.com", "password-123").assign(realm, ownerRole);
        findUserByName(domain, "angieparr@gmail.com").assign(realm, guestRole);


        // This is Jesse's checking account
        addUser(domain, "jedijes@gmail.com", "password-123").assign(realm, ownerRole);
        findUserByName(domain, "me@jacobparr.com").assign(realm, guestRole);

        this.domainProfiles.add(domain);
    }

    private void createTimeAndBilling() {
        DomainProfileEo domain = DomainProfileEo.create(SPENDING_FYI_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("harlan.work@gmail.com", "9876543210");
        domain.setAccessToken("time-and-billing-client", "9876543210");

        PolicyEo policy = domain.addPolicy("default");
        RealmEo realm = policy.addRealm("default");
        RoleEo role = policy.addRole("user");

        addUser(domain, "harlan.work@gmail.com", "password-123").assign(realm, role);
    }

    private IdentityEo addUser(DomainProfileEo domainProfile, String username, String password) {
        IdentityEo user = IdentityEo.create(domainProfile, username, password);
        users.add(user);
        return user;
    }

    private void createPhotoLab() {
        DomainProfileEo domain = DomainProfileEo.create(PHOTO_LAB_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("me@jacobparr.com", "9876543210");
        domain.setAccessToken("harlan.work@gmail.com", "9876543210");
        domain.setAccessToken("photo-lab-client", "9876543210");

        // Create the users of this profile
        IdentityEo jacob = addUser(domain, "me@jacobparr.com", "password-123");
        IdentityEo harlan = addUser(domain, "harlan.work@gmail.com", "password-123");
        IdentityEo rich = addUser(domain, "rich@westcoastimaging.com", "password-123");
        IdentityEo angie = addUser(domain, "angieparr@gmail.com", "password-123");
        IdentityEo brit = addUser(domain, "tigerspanda1994@gmail.com", "password-123");
        IdentityEo jesse = addUser(domain, "jedijes@gmail.com", "password-123");
        IdentityEo joe = addUser(domain, "joseph2jsh@gmail.com", "password-123");
        IdentityEo hannah = addUser(domain, "hn.noon@gmail.com", "password-123");

        // Photo Lab is not multi-tenant, so we addRealm the one "default" realm.
        PolicyEo tenayaPolicy = domain.addPolicy("tenaya");
        PolicyEo glacierPolicy = domain.addPolicy("glacier");
        PolicyEo basecampPolicy = domain.addPolicy("basecamp");

        RealmEo tenayaRealm = tenayaPolicy.addRealm("realm");
        RealmEo glacierRealm = glacierPolicy.addRealm("realm");
        RealmEo basecampRealm = basecampPolicy.addRealm("realm");

        RoleEo tenayaAdminRole = tenayaPolicy.addRole("admin");
        RoleEo glacierAdminRole = glacierPolicy.addRole("admin");
        RoleEo basecampAdminRole = basecampPolicy.addRole("admin");

        RoleEo photographer = glacierPolicy.addRole("photographer");

        RoleEo consumer = tenayaPolicy.addRole("consumer");

        // administrators
        for (IdentityEo identity : Arrays.asList(jacob, harlan, rich)) {
            identity.assign(tenayaRealm, tenayaAdminRole);
            identity.assign(glacierRealm, glacierAdminRole);
            identity.assign(basecampRealm, basecampAdminRole);
        }

        // photographers
        for (IdentityEo identity : Arrays.asList(jacob, harlan, rich, angie, hannah)) {
            identity.assign(glacierRealm, photographer);
        }

        // consumers
        for (IdentityEo identity : Arrays.asList(jacob, harlan, rich, angie, hannah, brit, jesse, joe)) {
            identity.assign(tenayaRealm, consumer);
        }
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
    public void addUser(IdentityEo user) {
        users.add(user);
    }

    public List<IdentityEo> getUsers(String username) {
        if (StringUtils.isBlank(username)) {
            return unmodifiableList(users);

        } else {
            List<IdentityEo> usersList = users.stream().filter(user -> objectsEqual(username, user.getUsername())).collect(toList());
            return unmodifiableList(usersList);
        }
    }

    @Override
    public IdentityEo findUserByName(DomainProfileEo domainProfile, String username) {
        String domainName = domainProfile.getDomainName();
        for (IdentityEo user : users) {
            if (objectsEqual(domainName, user.getDomainName()) &&
                objectsEqual(username, user.getUsername())) {

                return user;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }

    public IdentityEo findUserById(String id) {
        for (IdentityEo user : users) {
            if (objectsEqual(id, user.getId())) {
                return user;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }
}
