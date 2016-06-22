package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.identity.kernel.domain.RealmEo;
import org.tiogasolutions.identity.kernel.domain.RoleEo;
import org.tiogasolutions.identity.kernel.domain.TenantProfileEo;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;

public class TenantStore {

    private final Map<String,TenantProfileEo> profilesById = new HashMap<>();
    private final Map<String,TenantProfileEo> profilesByToken = new HashMap<>();

    public TenantStore() {

        TenantProfileEo tenantProfile = new TenantProfileEo(
                TimeUuid.randomUUID().toString(),
                null, "photo-lab",
                TenantStatus.ACTIVE, "api-token-324234234",
                "identity-photo-lab", emptyList(), emptyList());

        RealmEo tenayaRealm = tenantProfile.createRealm("TENAYA");
        RoleEo customerRole = tenayaRealm.createRole("CUSTOMER");

        RealmEo glacierRealm = tenantProfile.createRealm("GLACIER");
        RoleEo photographerRole = glacierRealm.createRole("PHOTOGRAPHER");

        RealmEo basecampRealm = tenantProfile.createRealm("BASECAMP");
        RoleEo adminRole = basecampRealm.createRole("ADMIN");

        tenantProfile.createUser("jacobp", "secret", customerRole, photographerRole, adminRole);
        tenantProfile.createUser("harlann", "secret", customerRole, photographerRole, adminRole);

        profilesById.put(tenantProfile.getProfileId(), tenantProfile);
        profilesByToken.put(tenantProfile.getApiToken(), tenantProfile);
    }

    public int countAll() {
        return profilesById.size();
    }

    public TenantProfileEo findByProfileId(String profileId) {
        return this.profilesById.get(profileId);
    }

    public TenantProfileEo findByToken(String token) {
        return this.profilesByToken.get(token);
    }
}
