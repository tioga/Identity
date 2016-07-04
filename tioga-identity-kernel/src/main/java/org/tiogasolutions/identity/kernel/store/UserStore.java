package org.tiogasolutions.identity.kernel.store;

import org.tiogasolutions.identity.kernel.domain.UserEo;

public interface UserStore {

    void addUser(UserEo user);

    UserEo findUserById(String userId);

    UserEo findUserByName(String username);
}
