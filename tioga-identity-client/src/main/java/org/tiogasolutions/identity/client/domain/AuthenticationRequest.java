package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationRequest {

    private final String domain;
    private final String username;
    private final String password;

    public AuthenticationRequest(@JsonProperty("domain") String domain,
                                 @JsonProperty("username") String username,
                                 @JsonProperty("password") String password) {
        this.domain = domain;
        this.username = username;
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
