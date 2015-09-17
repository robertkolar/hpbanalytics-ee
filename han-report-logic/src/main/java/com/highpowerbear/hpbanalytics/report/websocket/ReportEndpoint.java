package com.highpowerbear.hpbanalytics.report.websocket;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 17.9.2015.
 */
@ServerEndpoint("/websocket/report")
public class ReportEndpoint {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private WebsocketController websocketController;

    @OnOpen
    public void addSesssion(Session session) {
        l.fine("Websocket connection opened");
        websocketController.getReportSessions().add(session);
    }

    @OnMessage
    public void echo(Session session, String message) {
        l.fine("Websocket message received " + message);
        websocketController.sendReportMessage(session, message);
    }

    @OnError
    public void logError(Throwable t) {
        l.log(Level.SEVERE, "Websocket error", t);
    }

    @OnClose
    public void removeSession(Session session) {
        l.fine("Websocket connection closed");
        websocketController.getReportSessions().remove(session);
    }
}
