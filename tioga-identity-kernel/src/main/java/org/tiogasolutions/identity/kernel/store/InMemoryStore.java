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
    private static final String ACK_IM_DOMAIN = "ack-im";
    private static final String ACME_SYSTEMS_DOMAIN = "acme-systems";
    private static final String DISNEY_DOMAIN = "disney";

    public InMemoryStore() {
        reset();
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
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByUsername(domain, "harlan.work@gmail.com").assign(realm, ownerRole);
        addUser(domain, "photo-lab-client", "password-123").assign(realm, apiRole);

        realm = policy.addRealm(ACK_IM_DOMAIN);
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        addUser(domain, "chrisjasp@gmail.com", "password-123").assign(realm, ownerRole);
        addUser(domain, "ack-im-client", "password-123").assign(realm, ownerRole);

        realm = policy.addRealm(ACME_SYSTEMS_DOMAIN);
        addUser(domain, "acme-systems-client", "password-123").assign(realm, ownerRole);
        addUser(domain, "wile.e.coyote@acme-systems.com", "password-123").assign(realm, ownerRole);

        this.domainProfiles.add(domain);
    }

    public static final String DISNEY_CLIENT_TOKEN = "disney-9876543210";

    /** This systems is used explicitly in the unit tests and should not be removed */
    private void createDisney() {

        DomainProfileEo domain = DomainProfileEo.create(DISNEY_DOMAIN);
        domain.setAccessToken("disney-client", DISNEY_CLIENT_TOKEN);

        addUser(domain, "donald.duck@disney.com", "password-123");

        this.domainProfiles.add(domain);
    }

    public static final String ACME_SYSTEM_CLIENT_TOKEN = "acme-9876543210";

    /** This systems is used explicitly in the unit tests and should not be removed */
    private void createAcmeSystems() {

        DomainProfileEo domain = DomainProfileEo.create(ACME_SYSTEMS_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("acme-systems-client", ACME_SYSTEM_CLIENT_TOKEN);
        domain.setAccessToken("wile.e.coyote@acme-systems.com", "acme-0123456789");

        IdentityEo client = addUser(domain, "acme-systems-client", "password-123");
        IdentityEo mMouse = addUser(domain, "mickey.mouse@disney.com", "password-123");
        IdentityEo coyote = addUser(domain, "wile.e.coyote@acme-systems.com", "password-123");


        PolicyEo policy = domain.addPolicy("acme-hardware");

        RoleEo adminRole = policy.addRole("Administrator");
        adminRole.assign(policy.addPermission("DELETE"));
        adminRole.assign(policy.addPermission("REFUND"));

        RoleEo userRole = policy.addRole("User");
        userRole.assign(policy.addPermission("CANCEL"));
        userRole.assign(policy.addPermission("SEND"));

        RoleEo guestRole = policy.addRole("Guest");

        RealmEo secureRealm = policy.addRealm("Secure");
        RealmEo publicRealm = policy.addRealm("Public");

        client.assign(secureRealm, adminRole);

        coyote.assign(secureRealm, adminRole, userRole, guestRole);
        coyote.assign(publicRealm, userRole, guestRole);

        mMouse.assign(publicRealm, guestRole);

        this.domainProfiles.add(domain);
    }

    private void createAckIm() {

        DomainProfileEo domain = DomainProfileEo.create(ACK_IM_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("me@jacobparr.com", "ack-9876543210");
        domain.setAccessToken("chrisjasp@gmail.com", "ack-0123456789");
        domain.setAccessToken("ack-im-client", "ack-0918273645");

        PolicyEo policy = domain.addPolicy("default-policy");
        RealmEo realm = policy.addRealm("default-realm");
        RoleEo role = policy.addRole("default-role");

        addUser(domain, "me@jacobparr.com", "password-123").assign(realm, role);
        addUser(domain, "chrisjasp@gmail.com", "password-123").assign(realm, role);
        addUser(domain, "ack-im-client", "password-123").assign(realm, role);

        this.domainProfiles.add(domain);
    }

    private void createSpending() {

        DomainProfileEo domain = DomainProfileEo.create(SPENDING_FYI_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("spending-client", "fyi-9876543210");
        domain.setAccessToken("me@jacobparr.com", "fyi-0123456789");

        PolicyEo policy = domain.addPolicy("default-policy");

        RoleEo adminRole = policy.addRole("admin");
        RoleEo ownerRole = policy.addRole("owner");
        RoleEo guestRole = policy.addRole("guest");


        // This is my system, I get access to everyting!
        RealmEo globalReal = policy.addRealm("*");
        addUser(domain, "me@jacobparr.com", "password-123").assign(globalReal, adminRole);


        // These are Jacob & Angela's checking & savings accounts
        RealmEo realm = policy.addRealm("xxxx-2162");
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        addUser(domain, "angieparr@gmail.com", "password-123").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-0510");
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByUsername(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-3100");
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByUsername(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-1196");
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByUsername(domain, "angieparr@gmail.com").assign(realm, ownerRole);

        realm = policy.addRealm("xxxx-3730");
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, ownerRole);
        findIdentityByUsername(domain, "angieparr@gmail.com").assign(realm, ownerRole);


        // This is Brittany's checking account
        realm = policy.addRealm("xxxx-9186");
        addUser(domain, "tigerspanda1994@gmail.com", "password-123").assign(realm, ownerRole);
        findIdentityByUsername(domain, "angieparr@gmail.com").assign(realm, guestRole);


        // This is Jesse's checking account
        addUser(domain, "jedijes@gmail.com", "password-123").assign(realm, ownerRole);
        findIdentityByUsername(domain, "me@jacobparr.com").assign(realm, guestRole);

        this.domainProfiles.add(domain);
    }

    private void createTimeAndBilling() {
        DomainProfileEo domain = DomainProfileEo.create(TIME_AND_BILLING);
        // Hack the access tokens for testability
        domain.setAccessToken("harlan.work@gmail.com",   "tnb-9876543210");
        domain.setAccessToken("time-and-billing-client", "tnb-0123456789");

        PolicyEo policy = domain.addPolicy("default-policy");
        RealmEo realm = policy.addRealm("default-realm");
        RoleEo role = policy.addRole("user");

        addUser(domain, "harlan.work@gmail.com", "password-123").assign(realm, role);

        this.domainProfiles.add(domain);
    }

    private void createPhotoLab() {
        DomainProfileEo domain = DomainProfileEo.create(PHOTO_LAB_DOMAIN);
        // Hack the access tokens for testability
        domain.setAccessToken("me@jacobparr.com", "pl-9876543210");
        domain.setAccessToken("harlan.work@gmail.com", "pl-0123456789");
        domain.setAccessToken("photo-lab-client", "pl-0918273645");

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

    private IdentityEo addUser(DomainProfileEo domainProfile, String username, String password) {
        String id = domainProfile.getDomainName() + ":" + username;
        IdentityEo user = IdentityEo.createTest(domainProfile, username, password, id);
        identities.add(user);
        return user;
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
    public IdentityEo findIdentityByUsername(DomainProfileEo domainProfile, String username) {
        String domainName = domainProfile.getDomainName();
        for (IdentityEo identity : identities) {
            if (objectsEqual(domainName, identity.getDomainName()) &&
                objectsEqual(username, identity.getUsername())) {

                return identity;
            }
        }
        throw ApiException.notFound("The specified identity was not found.");
    }

    public IdentityEo findIdentityById(String id) {
        for (IdentityEo identity : identities) {
            if (objectsEqual(id, identity.getId())) {
                return identity;
            }
        }
        throw ApiException.notFound("The specified identity was not found.");
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

    public void reset() {
        domainProfiles.clear();
        identities.clear();

        createInternal();
        createPhotoLab();
        createSpending();
        createAckIm();
        createAcmeSystems();
        createTimeAndBilling();
        createDisney();
    }
}
