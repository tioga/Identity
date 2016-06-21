package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;

public class PubAccounts {

    public static final String DEFAULT_PAGE_SIZE = "3";

    private final int count;
    private final int total;
    private final int index;
    private final int pageSize;

    private final PubLinks _links;
    private final PubEmbedded _embedded;

    public PubAccounts(PubLinks _links, int count, int total, int index, int pageSize, PubEmbedded embedded) {
        this.total = total;
        this.count = count;

        this.index = index;
        this.pageSize = pageSize;

        this._links = _links;
        this._embedded = embedded;
    }

    @JsonIgnore
    public URI getSelf() {
        return _links.get("self").getHref();
    }

    @JsonIgnore
    public URI getFirst() {
        return _links.get("first").getHref();
    }

    @JsonIgnore
    public URI getPrevious() {
        return _links.get("previous").getHref();
    }

    @JsonIgnore
    public URI getNext() {
        return _links.get("next").getHref();
    }

    @JsonIgnore
    public URI getLast() {
        return _links.get("last").getHref();
    }

    public int getIndex() {
        return index;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PubEmbedded get_embedded() {
        return _embedded;
    }

    public int getCount() {
        return count;
    }

    public int getTotal() {
        return total;
    }

    public PubLinks get_links() {
        return _links;
    }
}
