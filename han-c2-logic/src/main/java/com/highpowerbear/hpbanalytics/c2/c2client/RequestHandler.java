package com.highpowerbear.hpbanalytics.c2.c2client;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.common.C2Util;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.PollEvent;
import com.highpowerbear.hpbanalytics.c2.entity.PublishEvent;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@Named
@ApplicationScoped
public class RequestHandler {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @Inject private RequestBuilder requestBuilder;
    @Inject private C2Dao c2Dao;

    public Integer getPosition(C2Signal c2Signal) {
        String c2Request = requestBuilder.buildPositionStatusRequest(c2Signal);
        String statusTag = C2ApiEnums.PositionStatusResponse.STATUS.getName();
        C2Definitions.PublishStatus success = C2Definitions.PublishStatus.POSOK;
        C2Definitions.PublishStatus fail = C2Definitions.PublishStatus.POSERR;
        Document dom = process(c2Signal, c2Request, statusTag, success, fail);
        Integer position = null;
        if (dom != null) {
            position = Integer.parseInt(dom.getElementsByTagName(C2ApiEnums.PositionStatusResponse.POSITION.getName()).item(0).getTextContent());
        }
        return position;
    }

    public boolean submit(C2Signal c2Signal) {
        String c2Request = requestBuilder.buildSignalRequest(c2Signal);
        String statusTag = C2ApiEnums.SignalResponse.STATUS.getName();
        C2Definitions.PublishStatus success = C2Definitions.PublishStatus.SBMOK;
        C2Definitions.PublishStatus fail = C2Definitions.PublishStatus.SBMERR;
        Document dom = process(c2Signal, c2Request, statusTag, success, fail);
        if (dom != null) {
            c2Signal = c2Dao.findC2Signal(c2Signal.getId()); // refresh from DB to prevent adding multiple events
            Integer signalId = Integer.parseInt(dom.getElementsByTagName(C2ApiEnums.SignalResponse.SIGNALID.getName()).item(0).getTextContent());
            c2Signal.setC2SignalId(signalId);
            c2Dao.updateC2Signal(c2Signal);
        }
        return (dom != null);
    }

    public boolean cancel(C2Signal c2Signal) {
        String c2Request = requestBuilder.buildCancelRequest(c2Signal);
        String statusTag = C2ApiEnums.CancelResponse.STATUS.getName();
        C2Definitions.PublishStatus success = C2Definitions.PublishStatus.CNCOK;
        C2Definitions.PublishStatus fail = C2Definitions.PublishStatus.CNCERR;
        return(process(c2Signal, c2Request, statusTag, success, fail) != null);
    }

    public boolean cancelOldBecauseUpdate(C2Signal c2Signal) {
        String c2Request = requestBuilder.buildCancelRequest(c2Signal);
        String statusTag = C2ApiEnums.CancelResponse.STATUS.getName();
        C2Definitions.PublishStatus success = C2Definitions.PublishStatus.UPDCNCOK;
        C2Definitions.PublishStatus fail = C2Definitions.PublishStatus.UPDCNCERR;
        return (process(c2Signal, c2Request, statusTag, success, fail) != null);
    }

    public boolean submitNewBecauseUpdate(C2Signal c2Signal) {
        String c2Request = requestBuilder.buildSignalRequest(c2Signal);
        String statusTag = C2ApiEnums.SignalResponse.STATUS.getName();
        C2Definitions.PublishStatus success = C2Definitions.PublishStatus.UPDSBMOK;
        C2Definitions.PublishStatus fail = C2Definitions.PublishStatus.UPDSBMERR;
        Document dom = process(c2Signal, c2Request, statusTag, success, fail);
        if (dom != null) {
            c2Signal = c2Dao.findC2Signal(c2Signal.getId()); // refresh from DB to prevent adding multiple events
            Integer signalId = Integer.parseInt(dom.getElementsByTagName(C2ApiEnums.SignalResponse.SIGNALID.getName()).item(0).getTextContent());
            c2Signal.setC2SignalId(signalId);
            c2Dao.updateC2Signal(c2Signal);
        }
        return (dom != null);
    }

    private Document process(C2Signal c2Signal, String c2Request, String statusTag, C2Definitions.PublishStatus success, C2Definitions.PublishStatus fail) {
        String c2Response;
        Document dom;
        URL url;
        try {
            url = new URL(c2Request);
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
            return null;
        }
        PublishEvent publishEvent = new PublishEvent();
        publishEvent.setC2Signal(c2Signal);
        publishEvent.setEventDate(Calendar.getInstance());
        publishEvent.setC2Request(c2Request);
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
            c2Response = C2Util.getXmlString(dom);
        }  catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
            publishEvent.setStatus(fail);
            c2Signal.addPublishEvent(publishEvent);
            c2Dao.updateC2Signal(c2Signal);
            return null;
        }
        publishEvent.setC2Response(c2Response);
        // check for C2 error status
        String responseStatus = dom.getElementsByTagName(statusTag).item(0).getTextContent();
        if (responseStatus.equals(C2ApiEnums.XmlStatusContent.ERROR.getName())) {
            publishEvent.setStatus(fail);
            c2Signal.addPublishEvent(publishEvent);
            c2Dao.updateC2Signal(c2Signal);
            return null;
        } else {
            publishEvent.setStatus(success);
            c2Signal.addPublishEvent(publishEvent);
            c2Dao.updateC2Signal(c2Signal);
            return dom;
        }
    }

    public void pollSignals(C2System c2System) {
        for (C2Signal c2Signal : c2Dao.getC2SignalsToPoll(c2System)) {
            pollSignal(c2Signal);
            if (C2Definitions.PollStatus.POLLERR.equals(c2Signal.getPollStatus())) {
                if (c2Dao.getPollErrorCount(c2Signal) > C2Definitions.C2_STATUS_POLLING_MAX_FAILED) {
                    PollEvent pollEvent = new PollEvent();
                    pollEvent.setC2Signal(c2Signal);
                    pollEvent.setEventDate(Calendar.getInstance());
                    pollEvent.setStatus(C2Definitions.PollStatus.UNKNOWN);
                    c2Dao.newPollEvent(pollEvent);
                }
            }
            c2Dao.updateC2Signal(c2Signal);
        }
    }

    private void pollSignal(C2Signal c2Signal) {
        URL url;
        String c2Response;
        Document dom;
        try {
            url = new URL(requestBuilder.buildSignalStatusRequest(c2Signal));
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
            return;
        }
        PollEvent pollEvent = new PollEvent();
        pollEvent.setC2Signal(c2Signal);
        pollEvent.setEventDate(Calendar.getInstance());
        pollEvent.setC2Request(requestBuilder.buildSignalStatusRequest(c2Signal));
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
            c2Response = C2Util.getXmlString(dom);
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
            pollEvent.setStatus(C2Definitions.PollStatus.POLLERR);
            c2Dao.newPollEvent(pollEvent);
            return;
        }
        pollEvent.setC2Response(c2Response);
        // check for C2 error status
        Node responseStatusNode = dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.STATUS.getName()).item(0);
        if (responseStatusNode != null && responseStatusNode.getTextContent().equals(C2ApiEnums.XmlStatusContent.ERROR.getName())) {
            l.log(Level.SEVERE, "Unable to C2RequestHandler.pollSignal, signalId=" + c2Signal.getC2SignalId() + ", C2 responded with error status code");
            pollEvent.setStatus(C2Definitions.PollStatus.POLLERR);
            c2Dao.newPollEvent(pollEvent);
            return;
        }
        pollEvent.setDatePosted(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.POSTEDWHEN.getName()).item(0).getTextContent());
        pollEvent.setDateEmailed(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.EMAILEDWHEN.getName()).item(0).getTextContent());
        pollEvent.setDateKilled(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.KILLEDWHEN.getName()).item(0).getTextContent());
        pollEvent.setDateExpired(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.EXPIREDWHEN.getName()).item(0).getTextContent());
        pollEvent.setDateTraded(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.TRADEDWHEN.getName()).item(0).getTextContent());
        pollEvent.setTradePrice(Double.parseDouble(dom.getElementsByTagName(C2ApiEnums.SignalStatusResponse.TRADEPRICE.getName()).item(0).getTextContent()));
        pollEvent.setStatus(C2Definitions.PollStatus.WORKING);
        if (!pollEvent.getDateKilled().equals("0")) {
            pollEvent.setStatus(C2Definitions.PollStatus.CANCELLED);
        } else if (!pollEvent.getDateExpired().equals("0")) {
            pollEvent.setStatus(C2Definitions.PollStatus.EXPIRED);
        } else if (!pollEvent.getDateTraded().equals("0")) {
            pollEvent.setStatus(C2Definitions.PollStatus.FILLED);
        }
        c2Dao.newPollEvent(pollEvent);
    }
}