package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLink;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class PubUsers extends PubItem {

    public static final String DEFAULT_LIMIT = "10";

    private final int included;
    private final int total;
    private final int offset;
    private final int limit;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<PubUser> items = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<PubLink> links = new ArrayList<>();

    public PubUsers(@JsonProperty("_status") PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("included") int included,
                    @JsonProperty("total") int total,
                    @JsonProperty("offset") int offset,
                    @JsonProperty("limit") int limit,
                    @JsonProperty("items") List<PubUser> items,
                    @JsonProperty("links") List<PubLink> links) {

        super(_status, _links);

        this.total = total;
        this.included = included;
        this.offset = offset;
        this.limit = limit;

        if (items != null) this.items.addAll(items);
        if (links != null) this.links.addAll(links);
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<PubUser> getItems() {
        return unmodifiableList(items);
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
