package org.tiogasolutions.identity.engine.mock;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

public class Account implements Serializable {

    private final String id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;

    @JsonCreator
    public Account(String id, String email, String firstName, String lastName, String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }
}
