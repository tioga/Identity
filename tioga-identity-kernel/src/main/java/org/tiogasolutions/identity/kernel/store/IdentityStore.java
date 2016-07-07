package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;

import java.util.List;

public interface IdentityStore {

    void addIdentity(IdentityEo identity);

    IdentityEo findIdentityById(String identityId);

    IdentityEo findIdentityByName(DomainProfileEo domainProfile, String username);

    List<IdentityEo> getAllIdentities(DomainProfileEo domainProfile, int offset, int limit);
}
