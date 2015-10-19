package com.highpowerbear.hpbanalytics.iblogger.persistence;

import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.rest.model.IbOrderFilter;

import java.util.List;

/**
 * Created by robertk on 3/28/15.
 */
public interface IbLoggerDao {
    IbAccount findIbAccount(String accountId);
    List<IbAccount> getIbAccounts();
    IbAccount updateIbAccount(IbAccount ibAccount);
    List<IbOrder> getFilteredIbOrders(IbAccount ibAccount, IbOrderFilter filter, Integer start, Integer limit);
    Long getNumFilteredIbOrders(IbAccount ibAccount, IbOrderFilter filter);
    List<IbOrder> getIbOpenOrders(IbAccount ibAccount);
    void newIbOrder(IbOrder ibOrder);
    void updateIbOrder(IbOrder ibOrder);
    IbOrder getIbOrderByPermId(IbAccount ibAccount, Integer permId);
}