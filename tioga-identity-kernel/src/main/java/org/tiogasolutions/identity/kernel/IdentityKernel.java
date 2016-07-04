package org.tiogasolutions.identity.kernel;

import org.tiogasolutions.identity.kernel.domain.ClientEo;
import org.tiogasolutions.identity.kernel.domain.UserEo;
import org.tiogasolutions.identity.kernel.store.ClientStore;
import org.tiogasolutions.identity.kernel.store.UserStore;

import java.util.List;

public class IdentityKernel {

    private final ClientStore clientStore;
    private final UserStore userStore;
    private final ClientEo client;

    public IdentityKernel(ClientStore clientStore, UserStore userStore, ClientEo client) {
        this.client = client;
        this.userStore = userStore;
        this.clientStore = clientStore;
    }

    public ClientEo getClient() {
        throw new UnsupportedOperationException();
    }

    public List<UserEo> findUserByName(String username) {
        throw new UnsupportedOperationException();
    }

    public UserEo findUserById(String userId) {
        return userStore.findUserById(userId);
    }

    public String getDomainName() {
        return client.getClientName();
    }
}
