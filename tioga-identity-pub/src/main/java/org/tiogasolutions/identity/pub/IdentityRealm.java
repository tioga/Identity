package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class IdentityRealm extends PubItem {

    private final String id;
    private final String realmName;
    private final String policyName;

    public IdentityRealm(@JsonProperty("_status") PubStatus _status,
                         @JsonProperty("_links") PubLinks _links,
                         @JsonProperty("id") String id,
                         @JsonProperty("realmName") String realmName,
                         @JsonProperty("policyName") String policyName) {

        super(_status, _links);

        this.id = id;
        this.realmName = realmName;
        this.policyName = policyName;
    }

    public String getId() {
        return id;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getPolicyName() {
        return policyName;
    }
}
