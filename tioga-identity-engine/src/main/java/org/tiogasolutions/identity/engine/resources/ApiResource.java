package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.identity.pub.PubAccount;
import org.tiogasolutions.identity.pub.PubAccounts;
import org.tiogasolutions.identity.pub.PubItem;
import org.tiogasolutions.identity.pub.PubLinks;
import org.tiogasolutions.identity.engine.mock.Account;
import org.tiogasolutions.identity.engine.mock.AccountStore;
import org.tiogasolutions.identity.engine.mock.PubUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static org.tiogasolutions.identity.pub.PubAccounts.DEFAULT_PAGE_SIZE;

public class ApiResource {

    private final PubUtils utils;
    private final UriInfo uriInfo;
    private final AccountStore accountStore;

    public ApiResource(UriInfo uriInfo, AccountStore accountStore) {
        this.uriInfo = uriInfo;
        this.utils = new PubUtils(uriInfo);
        this.accountStore = accountStore;
    }

    @GET
    @Produces(InetMediaType.APPLICATION_JSON_VALUE)
    public Response getApiRoot(@DefaultValue("LINKS") @QueryParam("detailLevel") DetailLevel detailLevel) {

        PubLinks pubLinks = new PubLinks();
        URI uri = utils.getAccountsUri(0, PubAccounts.DEFAULT_PAGE_SIZE, detailLevel);
        pubLinks.add("accounts", uri);

        PubItem<PubLinks> pubItem = new PubItem<>(pubLinks);

        return utils.ok(pubItem);
    }

    @GET
    @Path("/accounts")
    @Produces(InetMediaType.APPLICATION_JSON_VALUE)
    public Response getAccounts(@DefaultValue("LINKS") @QueryParam("detailLevel") DetailLevel detailLevel,
                                @DefaultValue("0") @QueryParam("index") int index,
                                @DefaultValue(DEFAULT_PAGE_SIZE) @QueryParam("pageSize") int pageSize) throws Exception {

        int total = accountStore.countAll();

        List<Account> accounts = accountStore.getAll(index, pageSize);
        PubAccounts pubAccounts = utils.convert(detailLevel, accounts, index, pageSize, total);

        return utils.ok(pubAccounts, pubAccounts.get_links());
    }

    @GET
    @Path("/accounts/{accountId}")
    @Produces(InetMediaType.APPLICATION_JSON_VALUE)
    public Response getAccount(@DefaultValue("LINKS") @QueryParam("detailLevel") DetailLevel detailLevel,
                               @PathParam("accountId") String accountId) throws Exception {

        Account account = accountStore.findById(accountId);
        PubLinks links = utils.toLinks(account, detailLevel);

        if (DetailLevel.LINKS == detailLevel) {
            return utils.ok(new PubItem<>(links));

        } else {
            PubAccount pubAccount = utils.convert(account);
            return utils.ok(new PubItem<>(links, pubAccount));
        }
    }
}
