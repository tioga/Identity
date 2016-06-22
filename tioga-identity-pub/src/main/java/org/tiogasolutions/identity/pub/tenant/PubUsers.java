package org.tiogasolutions.identity.pub.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

public class PubUsers extends PubItem {

    public static final String DEFAULT_PAGE_SIZE = "10";

    private final int included;
    private final int total;
    private final int offset;
    private final int limit;

    private final List<PubUser> items = new ArrayList<>();

    public PubUsers(@JsonProperty("_status") PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("included") int included,
                    @JsonProperty("total") int total,
                    @JsonProperty("offset") int offset,
                    @JsonProperty("limit") int limit,
                    @JsonProperty("items") List<PubUser> items) {

        super(_status, _links);

        this.total = total;
        this.included = included;
        this.offset = offset;
        this.limit = limit;

        if (items != null) this.items.addAll(items);
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<PubUser> getItems() {
        return items;
    }

    public int getIncluded() {
        return included;
    }

    public int getTotal() {
        return total;
    }
}
