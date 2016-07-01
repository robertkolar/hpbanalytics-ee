package com.highpowerbear.hpbanalytics.iblogger.process;

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
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="*/5", timezone="US/Eastern", persistent=false)
    private void reconnect() {
        ibLoggerDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnection(ibAccount);
            if (!c.isConnected() && c.isMarkConnected()) {
                c.connect();
            }
        });
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="31", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        ibLoggerDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnection(ibAccount);
            if (c.isConnected()) {
                heartbeatControl.updateHeartbeats(ibAccount);
                ibController.requestOpenOrders(ibAccount);
            }
        });
    }
}