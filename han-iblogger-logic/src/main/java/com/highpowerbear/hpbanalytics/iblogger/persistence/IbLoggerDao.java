package com.highpowerbear.hpbanalytics.iblogger.persistence;

import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import java.util.List;

/**
 * Created by robertk on 3/28/15.
 */
public interface IbLoggerDao {
    IbAccount findIbAccount(String accountId);
    List<IbAccount> getIbAccounts();
    IbAccount updateIbAccount(IbAccount ibAccount);
    List<IbOrder> getIbOrders(IbAccount ibAccount, Integer start, Integer limit);
    Long getNumIbOrders(IbAccount ibAccount);
    List<IbOrder> getIbOpenOrders(IbAccount ibAccount);
    void newIbOrder(IbOrder ibOrder);
    void updateIbOrder(IbOrder ibOrder);
    IbOrder getIbOrderByPermId(IbAccount ibAccount, Integer permId);
}