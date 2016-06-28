package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class SystemEo {

    private final String id;
    private final String systemName;

    @JsonBackReference
    private final List<RealmEo> realms = new ArrayList<>();

    @JsonManagedReference
    private final ClientEo client;

    private SystemEo(ClientEo client,
                     @JsonProperty("id") String id,
                     @JsonProperty("systemName") String systemName,
                     @JsonProperty("realms") List<RealmEo> realms) {

        this.client = client;

        this.id = id;
        this.systemName = systemName;
        if (realms != null) this.realms.addAll(realms);
    }

    public String getId() {
        return id;
    }

    public String getSystemName() {
        return systemName;
    }

    public ClientEo getClient() {
        return client;
    }

    public List<RealmEo> getRealms() {
        return realms;
    }

    public RealmEo addRealm(String realmName) {
        RealmEo realm = RealmEo.createRealm(this, realmName);
        realms.add(realm);
        return realm;
    }

    public static SystemEo createSystem(ClientEo client, String systemName) {

        String id = client.getClientName() + ":" + systemName;

        return new SystemEo(
                client,
                id,
                systemName,
                emptyList());
    }

    public String getIdPath() {
        return getClient().getClientName() + ":" + getSystemName();
    }

    public String toString() {
        return getIdPath();
    }
}
