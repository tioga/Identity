package org.tiogasolutions.identity.client.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class PubLinks extends LinkedHashMap<String,PubLink> {

    public PubLinks(PubLink...links) {
        this(links == null ? Collections.emptyList() : Arrays.asList(links));
    }

    public PubLinks(List<PubLink> links) {
        for (PubLink link : links) {
            put(link.getRel(), link);
        }
    }

    @JsonCreator
    public PubLinks(Map<String,PubLink> links) {
        for (PubLink link : links.values()) {
            put(link.getRel(), link);
        }
    }

    @JsonIgnore
    public List<PubLink> getItems() {
        return new ArrayList<>(values());
    }

    public PubLink getLink(String rel) {
        return get(rel);
    }

    public boolean hasLink(String rel) {
        return getLink(rel) != null;
    }

    public PubLink add(String rel, String href) {
        if (href == null) return null;
        return put(rel, new PubLink(rel, href));
    }
    public PubLink add(String rel, String href, String title) {
        if (href == null) return null;
        return put(rel, new PubLink(rel, href, title));
    }
}