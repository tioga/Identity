package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PubItem<T> {

    private final PubLinks _links;
    private final T item;

    public PubItem(PubLinks _links) {
        this.item = null;
        this._links = _links;
    }

    public PubItem(PubLinks links, T item) {
        this.item = item;
        this._links = links;
    }

    public PubLinks get_links() {
        return _links;
    }

    @JsonUnwrapped
    public T getItem() {
        return item;
    }
}
