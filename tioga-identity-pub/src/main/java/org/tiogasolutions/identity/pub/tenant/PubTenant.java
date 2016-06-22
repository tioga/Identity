package org.tiogasolutions.identity.pub.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.core.TenantStatus;

public class PubTenant extends PubItem {

    private final String profileId;
    private final String revision;
    private final String tenantName;
    private final TenantStatus tenantStatus;
    private final String tenantDbName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final PubUsers pubUsers;

    public PubTenant(@JsonProperty("_status")PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("profileId") String profileId,
                     @JsonProperty("revision") String revision,
                     @JsonProperty("tenantName") String tenantName,
                     @JsonProperty("tenantStatus") TenantStatus tenantStatus,
                     @JsonProperty("tenantDbName") String tenantDbName,
                     @JsonProperty("pubUsers") PubUsers pubUsers) {

        super(_status, _links);

        this.profileId = profileId;
        this.revision = revision;
        this.tenantName = tenantName;
        this.tenantStatus = tenantStatus;
        this.tenantDbName = tenantDbName;
        this.pubUsers = pubUsers;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getRevision() {
        return revision;
    }

    public String getTenantName() {
        return tenantName;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
    }

    public String getTenantDbName() {
        return tenantDbName;
    }

    public PubUsers getPubUsers() {
        return pubUsers;
    }
}
