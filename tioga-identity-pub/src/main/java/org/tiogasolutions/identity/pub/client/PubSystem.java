package org.tiogasolutions.identity.pub.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

public class PubSystem extends PubItem {

    private final String name;
    private final String clientName;

    @JsonCreator
    public PubSystem(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("name") String name,
                     @JsonProperty("clientName") String clientName) {

        super(_status, _links);

        this.name = name;
        this.clientName = clientName;
    }

    public String getName() {
        return name;
    }

    public String getClientName() {
        return clientName;
    }
}
