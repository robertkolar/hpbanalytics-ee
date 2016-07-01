package com.highpowerbear.hpbanalytics.iblogger.rest;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.HeartbeatControl;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbController;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.highpowerbear.hpbanalytics.iblogger.rest.model.IbOrderFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by robertk on 3/28/15.
 */
@Path("iblogger")
@ApplicationScoped
public class IbLoggerService {

    @Inject private IbLoggerDao ibloggerDao;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private FilterParser filterParser;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts")
    public RestList<IbAccount> getIbAccounts() {
        List<IbAccount> ibAccounts = ibloggerDao.getIbAccounts();
        ibAccounts.forEach(ibAccount -> ibAccount.setIbConnection(ibController.getIbConnection(ibAccount)));
        return new RestList<>(ibAccounts, (long) ibAccounts.size());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts")
    public IbAccount updateIbAccount(IbAccount ibAccount) {
        IbAccount ibAccountDb = ibloggerDao.findIbAccount(ibAccount.getAccountId());
        if (ibAccountDb == null) {
            return null;
        }
        return ibloggerDao.updateIbAccount(ibAccount);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/connect/{connect}")
    public IbAccount connecIbAccount(@PathParam("accountId") String accountId, @PathParam("connect") Boolean connect) {
        IbAccount ibAccount = ibloggerDao.findIbAccount(accountId);
        if (connect) {
            ibController.connect(ibAccount);
        } else {
            ibController.disconnect(ibAccount);
        }
        IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND);
        ibAccount.setIbConnection(ibController.getIbConnection(ibAccount));
        return ibAccount;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/iborders")
    public Response getFilteredIbOrders(
            @PathParam("accountId") String accountId,
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start != null ? start : 0);
        limit = (limit != null ? limit : IbLoggerDefinitions.JPA_MAX_RESULTS);
        List<IbOrder> ibOrders = new ArrayList<>();
        IbAccount ibAccount = ibloggerDao.findIbAccount(accountId);
        if (ibAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        IbOrderFilter filter = filterParser.parseIbOrderFilter(jsonFilter);
        for (IbOrder ibOrder : ibloggerDao.getFilteredIbOrders(ibAccount, filter, start, limit)) {
            Map<IbOrder, Integer> hm = heartbeatControl.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
            ibOrder.setHeartbeatCount(hm.get(ibOrder));
            ibOrders.add(ibOrder);
        }
        return Response.ok(new RestList<>(ibOrders, ibloggerDao.getNumFilteredIbOrders(ibAccount, filter))).build();
    }
}
