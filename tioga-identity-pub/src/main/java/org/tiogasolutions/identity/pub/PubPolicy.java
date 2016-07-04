package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class PubPolicy extends PubItem {

    private final String id;
    private final String policyName;
    private final String domainName;

    private final List<PubRealm> realms = new ArrayList<>();

    @JsonCreator
    public PubPolicy(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("id") String id,
                     @JsonProperty("policyName") String policyName,
                     @JsonProperty("domainName") String domainName,
                     @JsonProperty("realms") List<PubRealm> realms) {

        super(_status, _links);

        this.id = id;
        this.policyName = policyName;
        this.domainName = domainName;

        if (realms != null) this.realms.addAll(realms);
    }

    public String getId() {
        return id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<PubRealm> getRealms() {
        return unmodifiableList(realms);
    }
}
