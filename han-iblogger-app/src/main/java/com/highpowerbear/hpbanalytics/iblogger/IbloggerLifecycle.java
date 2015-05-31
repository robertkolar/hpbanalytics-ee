package com.highpowerbear.hpbanalytics.iblogger;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.SingletonRepo;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.HeartbeatControl;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbController;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbloggerDao;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@Singleton
@Startup
public class IbloggerLifecycle {
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Inject private SingletonRepo singletonRepo;
    @Inject private IbloggerDao ibloggerDao;
    @Inject private IbloggerData ibloggerData;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private IbController ibController;

    public void startup() {
        l.info("BEGIN IbloggerLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        for (IbAccount ibAccount : ibloggerDao.getIbAccounts()) {
            ibloggerData.getIbConnectionMap().put(ibAccount, new IbConnection());
            ibloggerData.getOpenOrderHeartbeatMap().put(ibAccount, new HashMap<>());
            heartbeatControl.init(ibAccount);
        }
        l.info("END IbloggerLifecycle.startup");
    }

    public void shutdown() {
        l.info("BEGIN IbloggerLifecycle.shutdown");
        ibloggerData.getIbConnectionMap().keySet().forEach(ibController::disconnect);
        l.info("END IbloggerLifecycle.shutdown");
    }
}
