package org.tiogasolutions.identity.kernel;

import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.UserStore;

import java.util.List;

public class IdentityKernel {

    private final DomainStore domainStore;
    private final UserStore userStore;
    private final DomainProfileEo domainProfile;

    public IdentityKernel(DomainStore domainStore, UserStore userStore, DomainProfileEo domainProfile) {
        this.domainProfile = domainProfile;
        this.userStore = userStore;
        this.domainStore = domainStore;
    }

    public DomainProfileEo getDomainProfile() {
        return domainProfile;
    }

    public UserEo findUserByName(String username) {
        return userStore.findUserByName(username);
    }

    public UserEo findUserById(String userId) {
        return userStore.findUserById(userId);
    }

    public String getDomainName() {
        return domainProfile.getDomainName();
    }
}
