package org.tiogasolutions.identity.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class PubRealm extends PubItem {

    private final String id;
    private final String realmName;
    private final String domainName;
    private final String systemName;

    private final List<PubRole> roles = new ArrayList<>();

    public PubRealm(@JsonProperty("_status") PubStatus _status,
                    @JsonProperty("_links") PubLinks _links,
                    @JsonProperty("id") String id,
                    @JsonProperty("realmName") String realmName,
                    @JsonProperty("domainName") String domainName,
                    @JsonProperty("systemName") String systemName,
                    @JsonProperty("roles") List<PubRole> roles) {

        super(_status, _links);

        this.id = id;
        this.realmName = realmName;
        this.domainName = domainName;
        this.systemName = systemName;
        if (roles != null) this.roles.addAll(roles);
    }

    public String getId() {
        return id;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getSystemName() {
        return systemName;
    }

    public List<PubRole> getRoles() {
        return unmodifiableList(roles);
    }
}
