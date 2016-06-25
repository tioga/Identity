package org.tiogasolutions.identity.kernel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class SystemEo {

    private final String id;
    private final String name;
    private final String clientName;
    private final List<RealmEo> realms = new ArrayList<>();


    private SystemEo(@JsonProperty("id") String id,
                     @JsonProperty("name") String name,
                     @JsonProperty("clientName") String clientName,
                     @JsonProperty("realms") List<RealmEo> realms) {

        this.id = id;
        this.name = name;
        this.clientName = clientName;
        if (realms != null) this.realms.addAll(realms);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClientName() {
        return clientName;
    }

    public List<RealmEo> getRealms() {
        return realms;
    }

    public RealmEo createRealm(String realmName) {
        RealmEo system = new RealmEo(realmName, emptyList());
        realms.add(system);
        return system;
    }

    public static SystemEo createSystem(ClientEo clientEo, String name) {
        return new SystemEo(
                TimeUuid.randomUUID().toString(),
                name,
                clientEo.getName(),
                emptyList()
        );
    }
}
