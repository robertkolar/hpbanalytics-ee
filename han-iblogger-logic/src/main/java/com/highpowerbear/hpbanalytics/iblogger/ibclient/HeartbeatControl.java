package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbloggerDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * Created by robertk on 4/6/15.
 */
@Named
@ApplicationScoped
public class HeartbeatControl {
    @Inject private IbloggerData ibloggerData;
    @Inject private IbloggerDao ibloggerDao;

    public void init(IbAccount ibAccount) {
        ibloggerDao.getIbOpenOrders(ibAccount).forEach(this::addHeartbeat);
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = ibloggerData.getOpenOrderHeartbeatMap().get(ibAccount);
        for (IbOrder ibOrder : hm.keySet()) {
            Integer failedHeartbeatsLeft = hm.get(ibOrder);
            if (failedHeartbeatsLeft <= 0) {
                if (!IbloggerDefinitions.IbOrderStatus.UNKNOWN.equals(ibOrder.getStatus())) {
                    ibOrder.addEvent(IbloggerDefinitions.IbOrderStatus.UNKNOWN, null, null);
                    ibloggerDao.updateIbOrder(ibOrder);
                }
                hm.remove(ibOrder);
            } else {
                hm.put(ibOrder, failedHeartbeatsLeft - 1);
            }
        }
    }

    public void heartbeatReceived(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = ibloggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.put(ibOrder, (failedHeartbeatsLeft < IbloggerDefinitions.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }

    public void addHeartbeat(IbOrder ibOrder) {
        ibloggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount()).put(ibOrder, IbloggerDefinitions.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = ibloggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.remove(ibOrder);
        }
    }
}