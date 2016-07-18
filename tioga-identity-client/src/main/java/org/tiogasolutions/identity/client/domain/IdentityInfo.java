package org.tiogasolutions.identity.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubLinks;

public class IdentityInfo extends PubItem {

    private final String upTime;

    public IdentityInfo(@JsonProperty("httpStatusCode") HttpStatusCode httpStatusCode,
                        @JsonProperty("links") PubLinks links,
                        @JsonProperty("elapsed") long elapsed) {

        super(httpStatusCode, links);

        this.upTime = String.format("%s days, %s hours, %s minutes, %s seconds",
            elapsed / (24 * 60 * 60 * 1000),
            elapsed / (60 * 60 * 1000) % 24,
            elapsed / (60 * 1000) % 60,
            elapsed / 1000 % 60);
    }

    public String getUpTime() {
        return upTime;
    }
}
