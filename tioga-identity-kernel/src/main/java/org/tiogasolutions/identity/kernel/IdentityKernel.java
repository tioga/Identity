package org.tiogasolutions.identity.kernel;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;
import org.tiogasolutions.identity.kernel.domain.IdentityEo;
import org.tiogasolutions.identity.kernel.store.DomainStore;
import org.tiogasolutions.identity.kernel.store.IdentityStore;

import java.util.List;

public class IdentityKernel {

    private final DomainStore domainStore;
    private final IdentityStore identityStore;

    private final DomainProfileEo currentDomainProfile;

    public IdentityKernel(DomainStore domainStore, IdentityStore identityStore, DomainProfileEo currentDomainProfile) {
        this.currentDomainProfile = currentDomainProfile;
        this.identityStore = identityStore;
        this.domainStore = domainStore;
    }

    public DomainProfileEo getCurrentDomainProfile() {
        return currentDomainProfile;
    }

    public IdentityEo findIdentityByUsername(DomainProfileEo domainProfile, String username) {
        return identityStore.findIdentityByUsername(domainProfile, username);
    }

    public IdentityEo findIdentityById(String userId) {
        return identityStore.findIdentityById(userId);
    }

    public List<IdentityEo> getAllIdentities(DomainProfileEo domainProfile, int offset, int limit) {
        return identityStore.getAllIdentities(domainProfile, offset, limit);
    }

    public DomainProfileEo findDomainByName(String name) {
        return domainStore.findByName(name);
    }

    public DomainProfileEo createDomainToken(String domainName, String username) {

        if (StringUtils.isBlank(domainName)) {
            throw ApiException.badRequest("The property \"domainName\" must be specified.");

        } else if (StringUtils.isBlank(domainName)) {
            throw ApiException.badRequest("The property \"username\" must be specified.");
        }

        DomainProfileEo domain = domainStore.findByName(domainName);

        if (domain == null) {
            throw ApiException.notFound("The specified domain doesn't exist.");
        }

        domain.generateAccessToken(username);
        domainStore.update(domain);

        return domain;
    }

    public List<DomainProfileEo> getAllDomains() {
        return domainStore.getAll();
    }

    public void update(DomainProfileEo domainProfile) {
        domainStore.update(domainProfile);
    }
}
