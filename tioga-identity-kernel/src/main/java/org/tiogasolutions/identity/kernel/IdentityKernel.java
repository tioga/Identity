package org.tiogasolutions.identity.kernel;

import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;

import java.util.List;

public class IdentityKernel {

    private final DomainStore domainStore;
    private final IdentityStore identityStore;
    private final DomainProfileEo domainProfile;

    public IdentityKernel(DomainStore domainStore, IdentityStore identityStore, DomainProfileEo domainProfile) {
        this.domainProfile = domainProfile;
        this.identityStore = identityStore;
        this.domainStore = domainStore;
    }

    public DomainProfileEo getDomainProfile() {
        return domainProfile;
    }

    public IdentityEo findUserByName(String username) {
        return identityStore.findIdentityByName(domainProfile, username);
    }

    public IdentityEo findUserById(String userId) {
        return identityStore.findIdentityById(userId);
    }

    public List<IdentityEo> getAllIdentities(int offset, int limit) {
        return identityStore.getAllIdentities(domainProfile, offset, limit);
    }

    public String getDomainName() {
        return domainProfile.getDomainName();
    }
}
