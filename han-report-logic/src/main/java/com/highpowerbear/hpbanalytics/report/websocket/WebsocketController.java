package com.highpowerbear.hpbanalytics.report.websocket;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 17.9.2015.
 */
@Named
@ApplicationScoped
public class WebsocketController {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    private Set<Session> reportSessions = new HashSet<>();

    public Set<Session> getReportSessions() {
        return reportSessions;
    }

    public void sendReportMessage(Session s, String message) {
        try {
            s.getBasicRemote().sendText(message);
        } catch (Throwable ioe) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, ioe);
        }
    }

    public void broadcastReportMessage(String message) {
        reportSessions.stream().filter(Session::isOpen).forEach(s -> sendReportMessage(s, message));
    }
}
