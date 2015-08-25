package com.highpowerbear.hpbanalytics.iblogger.common;

import com.highpowerbear.hpbanalytics.iblogger.conversion.OutputProcessor;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.HeartbeatControl;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.OpenOrderHandler;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.highpowerbear.hpbanalytics.iblogger.websocket.WebsocketController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by robertk on 3/8/15.
 */
@Named
@ApplicationScoped
public class SingletonRepo {
    private static SingletonRepo srepo;

    // should be used only within lifecycle to initialize
    public static void setInstance(SingletonRepo instance) {
        srepo = instance;
    }
    // should be used only in cases where CDI cannot be used
    public static SingletonRepo getInstance() {
        return srepo;
    }

    @Inject private IbLoggerDao ibLoggerDao;
    @Inject private IbLoggerData ibLoggerData;
    @Inject private OpenOrderHandler openOrderHandler;
    @Inject private OutputProcessor outputProcessor;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private WebsocketController websocketController;

    public IbLoggerDao getIbLoggerDao() {
        return ibLoggerDao;
    }

    public IbLoggerData getIbLoggerData() {
        return ibLoggerData;
    }

    public OpenOrderHandler getOpenOrderHandler() {
        return openOrderHandler;
    }

    public OutputProcessor getOutputProcessor() {
        return outputProcessor;
    }

    public HeartbeatControl getHeartbeatControl() {
        return heartbeatControl;
    }

    public WebsocketController getWebsocketController() {
        return websocketController;
    }
}