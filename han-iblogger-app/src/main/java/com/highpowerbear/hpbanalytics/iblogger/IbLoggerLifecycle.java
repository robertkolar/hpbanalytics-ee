package com.highpowerbear.hpbanalytics.iblogger;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.SingletonRepo;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.HeartbeatControl;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbController;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
public class IbLoggerLifecycle {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private SingletonRepo singletonRepo;
    @Inject private IbLoggerDao ibloggerDao;
    @Inject private IbLoggerData ibloggerData;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private IbController ibController;

    @PostConstruct
    public void startup() {
        l.info("BEGIN IbLoggerLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        for (IbAccount ibAccount : ibloggerDao.getIbAccounts()) {
            ibloggerData.getIbConnectionMap().put(ibAccount, new IbConnection());
            ibloggerData.getOpenOrderHeartbeatMap().put(ibAccount, new HashMap<>());
            heartbeatControl.init(ibAccount);
        }
        l.info("END IbLoggerLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN IbLoggerLifecycle.shutdown");
        ibloggerData.getIbConnectionMap().keySet().forEach(ibController::disconnect);
        l.info("END IbLoggerLifecycle.shutdown");
    }
}
