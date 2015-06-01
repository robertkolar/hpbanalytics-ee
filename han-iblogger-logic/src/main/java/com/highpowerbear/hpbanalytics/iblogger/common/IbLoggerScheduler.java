package com.highpowerbear.hpbanalytics.iblogger.common;

import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.HeartbeatControl;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbController;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by robertk on 3/29/15.
 */
@Singleton
public class IbLoggerScheduler {
    @Inject private IbLoggerDao ibLoggerDao;
    @Inject private IbLoggerData ibLoggerData;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="*/5", timezone="US/Eastern", persistent=false)
    private void reconnect() {
        for (IbAccount ibAccount : ibLoggerDao.getIbAccounts()) {
            IbConnection c = ibLoggerData.getIbConnectionMap().get(ibAccount);
            if (c == null) { // can happen at application startup when not fully initialized yet
                return;
            }
            if (c.getClientSocket() != null) {
                ibController.connect(ibAccount);
            }
        }
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="31", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        ibLoggerDao.getIbAccounts().stream().filter(ibController::isConnected).forEach(ibAccount -> {
            heartbeatControl.updateHeartbeats(ibAccount);
            ibController.requestOpenOrders(ibAccount);
        });
    }
}