package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.identity.kernel.domain.DomainProfileEo;

import java.util.List;

public interface DomainStore {

    DomainProfileEo findByName(String name);

    DomainProfileEo findByToken(String test);

    void update(DomainProfileEo domainProfile);

    List<DomainProfileEo> getAll();
}
