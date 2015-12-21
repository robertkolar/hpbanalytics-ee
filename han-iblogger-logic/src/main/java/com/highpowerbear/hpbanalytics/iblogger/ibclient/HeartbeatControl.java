package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/6/15.
 */
@Named
@ApplicationScoped
public class HeartbeatControl {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private IbLoggerData ibLoggerData;
    @Inject private IbLoggerDao ibLoggerDao;

    public void init(IbAccount ibAccount) {
        ibLoggerDao.getIbOpenOrders(ibAccount).forEach(this::addHeartbeat);
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = ibLoggerData.getOpenOrderHeartbeatMap().get(ibAccount);
        Set<IbOrder> keyset = new HashSet<>(hm.keySet());
        for (IbOrder ibOrder : keyset) {
            Integer failedHeartbeatsLeft = hm.get(ibOrder);
            if (failedHeartbeatsLeft <= 0) {
                if (!IbLoggerDefinitions.IbOrderStatus.UNKNOWN.equals(ibOrder.getStatus())) {
                    ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.UNKNOWN, null, null);
                    ibLoggerDao.updateIbOrder(ibOrder);
                }
                hm.remove(ibOrder);
            } else {
                hm.put(ibOrder, failedHeartbeatsLeft - 1);
            }
        }
    }

    public void heartbeatReceived(IbOrder ibOrder) {
        l.info("Heartbeat received for order, id=" + ibOrder.getId() + ", permId=" + ibOrder.getPermId() + ", orderId=" + ibOrder.getOrderId());
        Map<IbOrder, Integer> hm = ibLoggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.put(ibOrder, (failedHeartbeatsLeft < IbLoggerDefinitions.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }

    public void addHeartbeat(IbOrder ibOrder) {
        ibLoggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount()).put(ibOrder, IbLoggerDefinitions.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = ibLoggerData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.remove(ibOrder);
        }
    }
}