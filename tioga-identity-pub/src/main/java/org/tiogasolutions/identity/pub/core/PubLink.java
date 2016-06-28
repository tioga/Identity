package org.tiogasolutions.identity.pub.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PubLink {

    private final String href;
    private final String rel;
    private final String title;

    public PubLink(String rel, String href) {
        this(rel, href, null);
    }

    @JsonCreator
    public PubLink(@JsonProperty("rel") String rel,
                   @JsonProperty("href") String href,
                   @JsonProperty("title") String title) {

        this.rel = rel;
        this.href = href;
        this.title = title;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubLink pubLink = (PubLink) o;

        if (!href.equals(pubLink.href)) return false;
        if (!rel.equals(pubLink.rel)) return false;
        return title != null ? title.equals(pubLink.title) : pubLink.title == null;

    }

    @Override
    public int hashCode() {
        int result = href.hashCode();
        result = 31 * result + rel.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    public PubLink clone(String rel) {
        return new PubLink(rel, href, title);
    }
}
