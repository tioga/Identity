package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationRequest {

    private final String domainName;
    private final String username;
    private final String password;

    public AuthenticationRequest(@JsonProperty("domainName") String domainName,
                                 @JsonProperty("username") String username,
                                 @JsonProperty("password") String password) {

        this.domainName = domainName;
        this.username = username;
        this.password = password;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
