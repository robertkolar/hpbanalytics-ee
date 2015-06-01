package com.highpowerbear.hpbanalytics.iblogger.rest;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbController;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@Path("iblogger")
@ApplicationScoped
public class IbLoggerService {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private IbLoggerDao ibloggerDao;
    @Inject private IbController ibController;
    @Inject private IbLoggerData ibloggerData;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts")
    public RestList<IbAccount> getIbAccounts() {
        List<IbAccount> ibAccounts = new ArrayList<>();
        for (IbAccount ibAccount : ibloggerDao.getIbAccounts()) {
            ibAccount.setIbConnection(ibloggerData.getIbConnectionMap().get(ibAccount));
            ibAccount.getIbConnection().setIsConnected(ibController.isConnected(ibAccount));
            ibAccounts.add(ibAccount);
        }
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("iborders")
    public RestList<IbOrder> getIbOrders(@QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start != null ? start : 0);
        limit = (limit != null ? limit : IbLoggerDefinitions.JPA_MAX_RESULTS);
        List<IbOrder> ibOrders = new ArrayList<>();
        for (IbOrder ibOrder : ibloggerDao.getIbOrders(start, limit)) {
            Map<IbOrder, Integer> hm = ibloggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
            ibOrder.setHeartbeatCount(hm.get(ibOrder));
            ibOrders.add(ibOrder);
        }
        return new RestList<>(ibOrders, ibloggerDao.getNumIbOrders());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/connect/{connect}")
    public IbAccount connectAccount(@PathParam("accountId") String accountId, @PathParam("connect") Boolean connect) {
        IbAccount ibAccount = ibloggerDao.findIbAccount(accountId);
        if (connect) {
            ibController.connect(ibAccount);
        } else {
            ibController.disconnect(ibAccount);
        }
        IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND);
        ibAccount.setIbConnection(ibloggerData.getIbConnectionMap().get(ibAccount));
        ibAccount.getIbConnection().setIsConnected(ibController.isConnected(ibAccount));
        return ibAccount;
    }
}
