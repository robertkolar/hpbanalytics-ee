package com.highpowerbear.hpbanalytics.c2.common;

import com.highpowerbear.hpbanalytics.c2.c2client.RequestHandler;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
import com.highpowerbear.hpbanalytics.c2.websocket.WebsocketController;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by robertk on 2/9/14.
 */
@Singleton
public class C2Scheduler {
    @Inject private RequestHandler requestHandler;
    @Inject private C2Dao c2Dao;
    @Inject private WebsocketController websocketController;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="11", timezone="US/Eastern", persistent=false)
    private void pollSignals() {
        c2Dao.getC2Systems().forEach(requestHandler::pollSignals);
        websocketController.broadcastC2Message("c2 signals polled");
    }
}