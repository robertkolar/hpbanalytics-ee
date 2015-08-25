package com.highpowerbear.hpbanalytics.iblogger.websocket;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rkolar on 5/12/14.
 */
@ServerEndpoint("/websocket/iblogger")
public class IbLoggerEndpoint {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);
    @Inject private WebsocketController websocketController;

    @OnOpen
    public void addSesssion(Session session) {
        l.fine("Websocket connection opened");
        websocketController.getIbloggerSessions().add(session);
    }

    @OnMessage
    public void echo(Session session, String message) {
        l.fine("Websocket message received " + message);
        websocketController.sendIbLoggerMessage(session, message);
    }

    @OnError
    public void logError(Throwable t) {
        l.log(Level.SEVERE, "Websocket error", t);
    }

    @OnClose
    public void removeSession(Session session) {
        l.fine("Websocket connection closed");
        websocketController.getIbloggerSessions().remove(session);
    }
}