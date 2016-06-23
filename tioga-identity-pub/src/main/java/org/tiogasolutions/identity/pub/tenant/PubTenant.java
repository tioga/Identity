package org.tiogasolutions.identity.pub.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.identity.pub.core.PubItem;
import org.tiogasolutions.identity.pub.core.PubLinks;
import org.tiogasolutions.identity.pub.core.PubStatus;
import org.tiogasolutions.identity.pub.core.TenantStatus;

public class PubTenant extends PubItem {

    private final String id;
    private final String revision;
    private final String name;
    private final TenantStatus status;
    private final String dbName;

    public PubTenant(@JsonProperty("_status")PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("id") String id,
                     @JsonProperty("revision") String revision,
                     @JsonProperty("name") String name,
                     @JsonProperty("status") TenantStatus status,
                     @JsonProperty("dbName") String dbName) {

        super(_status, _links);

        this.id = id;
        this.revision = revision;
        this.name = name;
        this.status = status;
        this.dbName = dbName;
    }

    public String getId() {
        return id;
    }

    public String getRevision() {
        return revision;
    }

    public String getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public String getDbName() {
        return dbName;
    }
}
