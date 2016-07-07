package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLink;
import org.tiogasolutions.identity.client.core.PubLinks;
import org.tiogasolutions.identity.client.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class IdentityPolicies extends PubItem {

    private final int total;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<IdentityPolicy> items = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<PubLink> links = new ArrayList<>();

    public IdentityPolicies(@JsonProperty("_status") PubStatus _status,
                            @JsonProperty("_links") PubLinks _links,
                            @JsonProperty("total") int total,
                            @JsonProperty("items") List<IdentityPolicy> items,
                            @JsonProperty("links") List<PubLink> links) {

        super(_status, _links);

        this.total = total;

        if (items != null) this.items.addAll(items);
        if (links != null) this.links.addAll(links);
    }

    public List<IdentityPolicy> getItems() {
        return unmodifiableList(items);
    }

    public List<PubLink> getLinks() {
        return unmodifiableList(links);
    }

    public int getTotal() {
        return total;
    }
}
