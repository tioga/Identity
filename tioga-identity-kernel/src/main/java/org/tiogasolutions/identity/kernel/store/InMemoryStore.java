package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.tiogasolutions.dev.common.EqualsUtils.objectsEqual;
import static org.tiogasolutions.identity.kernel.domain.DomainProfileEo.INTERNAL_DOMAIN;

public class InMemoryStore implements DomainStore, IdentityStore {

    private final List<DomainProfileEo> domainProfiles = new ArrayList<>();
    private final List<IdentityEo> identities = new ArrayList<>();

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
        PolicyEo policy = domain.addPolicy("default-policy");

        RoleEo ownerRole = policy.addRole("owner");
        RoleEo apiRole = policy.addRole("api");

        RealmEo realm = policy.addRealm(SPENDING_FYI_DOMAIN);
        addUser(domain, "me@jacobparr.com", "password-123").assign(realm, ownerRole);
        addUser(domain, "spending-client", "password-123").assign(realm, apiRole);

        realm = policy.addRealm(TIME_AND_BILLING);
        addUser(domain, "harlan.work@gmail.com", "password-123").assign(realm, ownerRole);
        addUser(domain, "time-and-billing-client", "password-123").assign(realm, apiRole);

        realm = policy.addRealm(PHOTO_LAB_DOMAIN);
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByName(domain, "harlan.work@gmail.com").assign(realm, ownerRole);
        addUser(domain, "photo-lab-client", "password-123").assign(realm, apiRole);

        this.domainProfiles.add(domain);
    }

    private void createSpending() {

        DomainProfileEo domain = DomainProfileEo.create(SPENDING_FYI_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("spending-client", "9876543210");
        domain.setAccessToken("me@jacobparr.com", "9876543210");

        PolicyEo policy = domain.addPolicy("default-policy");

        RoleEo adminRole = policy.addRole("admin");
        RoleEo ownerRole = policy.addRole("owner");
        RoleEo guestRole = policy.addRole("guest");


        // This is my system, I get access to everyting!
        RealmEo globalReal = policy.addRealm("*");
        addUser(domain, "me@jacobparr.com", "password-123").assign(globalReal, adminRole);


        // These are Jacob & Angela's checking & savings accounts
        RealmEo realm = policy.addRealm("xxxx-2162");
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        addUser(domain, "angieparr@gmail.com", "password-123").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-0510");
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-3100");
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-1196");
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-3730");
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByName(domain, "angieparr@gmail.com").assign(realm, ownerRole);


        // This is Brittany's checking account
        realm = policy.addRealm("xxxx-9186");
        addUser(domain, "tigerspanda1994@gmail.com", "password-123").assign(realm, ownerRole);
        findIdentityByName(domain, "angieparr@gmail.com").assign(realm, guestRole);


        // This is Jesse's checking account
        addUser(domain, "jedijes@gmail.com", "password-123").assign(realm, ownerRole);
        findIdentityByName(domain, "me@jacobparr.com").assign(realm, guestRole);

        this.domainProfiles.add(domain);
    }

    private void createTimeAndBilling() {
        DomainProfileEo domain = DomainProfileEo.create(TIME_AND_BILLING);
        // Hack the access tokens for testability
        domain.setAccessToken("harlan.work@gmail.com", "9876543210");
        domain.setAccessToken("time-and-billing-client", "9876543210");

        PolicyEo policy = domain.addPolicy("default-policy");
        RealmEo realm = policy.addRealm("default-realm");
        RoleEo role = policy.addRole("user");

        addUser(domain, "harlan.work@gmail.com", "password-123").assign(realm, role);
    }

    private IdentityEo addUser(DomainProfileEo domainProfile, String username, String password) {
        IdentityEo user = IdentityEo.create(domainProfile, username, password);
        identities.add(user);
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
    public void addIdentity(IdentityEo identity) {
        identities.add(identity);
    }

    @Override
    public IdentityEo findIdentityByName(DomainProfileEo domainProfile, String username) {
        String domainName = domainProfile.getDomainName();
        for (IdentityEo identity : identities) {
            if (objectsEqual(domainName, identity.getDomainName()) &&
                objectsEqual(username, identity.getUsername())) {

                return identity;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }

    public IdentityEo findIdentityById(String id) {
        for (IdentityEo identity : identities) {
            if (objectsEqual(id, identity.getId())) {
                return identity;
            }
        }
        throw ApiException.notFound("The specified user was not found.");
    }

    @Override
    public List<IdentityEo> getAllIdentities(DomainProfileEo domainProfile, int offset, int limit) {
        List<IdentityEo> list = new ArrayList<>();

        String domainName = domainProfile.getDomainName();
        list.addAll(identities.stream()
                .filter(identity -> objectsEqual(domainName, identity.getDomainName()))
                .collect(Collectors.toList()));

        return list;
    }
}
