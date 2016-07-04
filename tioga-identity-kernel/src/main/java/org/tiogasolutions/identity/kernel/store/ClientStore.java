package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.identity.kernel.domain.ClientEo;

import java.util.List;

public interface ClientStore {

    ClientEo findByName(String name);

    ClientEo findByToken(String test);

    void update(ClientEo clientEo);

    List<ClientEo> getAll();
}
