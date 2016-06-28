package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class PubSystem extends PubItem {

    private final String id;
    private final String systemName;
    private final String clientName;

    private final List<PubRealm> realms = new ArrayList<>();

    @JsonCreator
    public PubSystem(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("id") String id,
                     @JsonProperty("systemName") String systemName,
                     @JsonProperty("clientName") String clientName,
                     @JsonProperty("realms") List<PubRealm> realms) {

        super(_status, _links);

        this.id = id;
        this.systemName = systemName;
        this.clientName = clientName;

        if (realms != null) this.realms.addAll(realms);
    }

    public String getId() {
        return id;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getClientName() {
        return clientName;
    }

    public List<PubRealm> getRealms() {
        return unmodifiableList(realms);
    }
}
