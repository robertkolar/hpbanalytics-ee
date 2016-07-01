package com.highpowerbear.hpbanalytics.c2.websocket;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 5/17/14.
 */
@ApplicationScoped
public class WebsocketController implements Serializable {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    private Set<Session> c2Sessions = new HashSet<>();

    public Set<Session> getC2Sessions() {
        return c2Sessions;
    }

    public void sendC2Message(Session s, String message) {
        try {
            s.getBasicRemote().sendText(message);
        } catch (Throwable ioe) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, ioe);
        }
    }

    public void broadcastC2Message(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + c2Sessions.size());
        c2Sessions.stream().filter(Session::isOpen).forEach(s -> sendC2Message(s, message));
    }
}