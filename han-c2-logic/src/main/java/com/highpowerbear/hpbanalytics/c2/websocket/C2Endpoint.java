package com.highpowerbear.hpbanalytics.c2.websocket;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rkolar on 5/12/14.
 */
@ServerEndpoint("/websocket/c2")
public class C2Endpoint {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);
    @Inject private WebsocketController websocketController;

    @OnOpen
    public void addSesssion(Session session) {
        l.fine("Websocket connection opened");
        websocketController.getC2Sessions().add(session);
    }

    @OnMessage
    public void echo(Session session, String message) {
        l.fine("Websocket message received " + message);
        websocketController.sendC2Message(session, message);
    }

    @OnError
    public void logError(Throwable t) {
        l.log(Level.SEVERE, "Websocket error", t);
    }

    @OnClose
    public void removeSession(Session session) {
        l.fine("Websocket connection closed");
        websocketController.getC2Sessions().remove(session);
    }
}