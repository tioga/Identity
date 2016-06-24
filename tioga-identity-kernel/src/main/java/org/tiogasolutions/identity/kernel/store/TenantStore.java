package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.identity.kernel.domain.RealmEo;
import org.tiogasolutions.identity.kernel.domain.RoleEo;
import org.tiogasolutions.identity.kernel.domain.TenantEo;
import org.tiogasolutions.identity.pub.core.TenantStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class TenantStore {

    private final List<TenantEo> tenants = new ArrayList<>();

    public TenantStore() {

        createSystem();
        createPhotoLab();
    }

    private void createSystem() {
        TenantEo tenant = new TenantEo(
                "system", null,
                TenantStatus.ACTIVE,
                "123", // TimeUuid.randomUUID().toString(),
                "password-123",
                "identity-photo-lab",
                emptyList(), emptyList());

        RealmEo systemRealm = tenant.createRealm("SYSTEM");
        RoleEo adminRole = systemRealm.createRole("ADMIN");

        tenant.createUser("jacobp", "secret", adminRole);
        tenant.createUser("harlann", "secret", adminRole);

        this.tenants.add(tenant);
    }

    private void createPhotoLab() {
        TenantEo tenant = new TenantEo(
                "photo-lab", null,
                TenantStatus.ACTIVE,
                TimeUuid.randomUUID().toString(),
                "password-123",
                "identity-photo-lab",
                emptyList(), emptyList());

        RealmEo tenayaRealm = tenant.createRealm("TENAYA");
        RoleEo customerRole = tenayaRealm.createRole("CUSTOMER");

        RealmEo glacierRealm = tenant.createRealm("GLACIER");
        RoleEo photographerRole = glacierRealm.createRole("PHOTOGRAPHER");

        RealmEo basecampRealm = tenant.createRealm("BASECAMP");
        RoleEo adminRole = basecampRealm.createRole("ADMIN");

        tenant.createUser("jacobp", "secret", customerRole, photographerRole, adminRole);
        tenant.createUser("harlann", "secret", customerRole, photographerRole, adminRole);

        this.tenants.add(tenant);
    }

    public int countAll() {
        return tenants.size();
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
}
