package com.highpowerbear.hpbanalytics.iblogger.websocket;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 5/17/14.
 */
@Named
@ApplicationScoped
public class WebsocketController implements Serializable {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    private Set<Session> ibloggerSessions = new HashSet<>();

    public Set<Session> getIbloggerSessions() {
        return ibloggerSessions;
    }

    public void sendIbLoggerMessage(Session s, String message) {
        try {
            s.getBasicRemote().sendText(message);
        } catch (Throwable ioe) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, ioe);
        }
    }

    public void broadcastIbLoggerMessage(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + ibloggerSessions.size());
        ibloggerSessions.stream().filter(Session::isOpen).forEach(s -> sendIbLoggerMessage(s, message));
    }
}