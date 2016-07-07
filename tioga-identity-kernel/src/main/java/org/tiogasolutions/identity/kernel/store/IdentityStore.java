package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;

public interface IdentityStore {

    void addUser(IdentityEo user);

    IdentityEo findUserById(String userId);

    IdentityEo findUserByName(DomainProfileEo domainProfile, String username);
}
