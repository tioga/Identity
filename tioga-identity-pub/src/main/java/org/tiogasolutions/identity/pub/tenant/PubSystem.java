package org.tiogasolutions.identity.pub.tenant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

public class PubSystem extends PubItem {

    private final String name;
    private final String tenantName;

    @JsonCreator
    public PubSystem(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("name") String name,
                     @JsonProperty("tenantName") String tenantName) {

        super(_status, _links);

        this.name = name;
        this.tenantName = tenantName;
    }

    public String getName() {
        return name;
    }

    public String getTenantName() {
        return tenantName;
    }
}
