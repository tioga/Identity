package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class PubLink {

    private final URI href;
    private final String rel;
    private final String title;

    public PubLink(String rel, URI href) {
        this(rel, href, null);
    }

    public PubLink(String rel, URI href, String title) {
        this.rel = rel;
        this.href = href;
        this.title = title;
    }

    @JsonCreator
    private PubLink(@JsonProperty("href") URI href,
                    @JsonProperty("title") String title) {
        this(null, href, title);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle() {
        return title;
    }

    public URI getHref() {
        return href;
    }

    @JsonIgnore
    public String getRel() {
        return rel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubLink pubLink = (PubLink) o;

        if (href != null ? !href.equals(pubLink.href) : pubLink.href != null) return false;
        return title != null ? title.equals(pubLink.title) : pubLink.title == null;

    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
