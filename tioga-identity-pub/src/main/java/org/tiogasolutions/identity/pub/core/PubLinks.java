package org.tiogasolutions.identity.pub.core;

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

    public List<PubLink> getLinks() {
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
