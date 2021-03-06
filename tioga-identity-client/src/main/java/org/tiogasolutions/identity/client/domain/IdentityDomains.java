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

public class IdentityDomains extends PubItem {

    public static final String DEFAULT_LIMIT = "10";

    private final int included;
    private final int total;
    private final int offset;
    private final int limit;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<PubLink> links = new ArrayList<>();

    public IdentityDomains(@JsonProperty("_status") PubStatus _status,
                           @JsonProperty("_links") PubLinks _links,
                           @JsonProperty("included") int included,
                           @JsonProperty("total") int total,
                           @JsonProperty("offset") int offset,
                           @JsonProperty("limit") int limit,
                           @JsonProperty("links") List<PubLink> links) {

        super(_status, _links);

        this.total = total;
        this.included = included;
        this.offset = offset;
        this.limit = limit;

        if (links != null) this.links.addAll(links);
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<PubLink> getLinks() {
        return unmodifiableList(links);
    }

    public int getIncluded() {
        return included;
    }

    public int getTotal() {
        return total;
    }
}
