package com.highpowerbear.hpbanalytics.c2.message;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.common.C2Util;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
import com.highpowerbear.hpbanalytics.c2.process.InputProcessor;
import com.highpowerbear.hpbanalytics.c2.websocket.WebsocketController;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = C2Definitions.IBLOGGER_TO_C2_QUEUE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        //@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
})
public class MqListenerBean implements MessageListener {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @Inject private C2Dao c2Dao;
    @Inject private InputProcessor inputProcessor;
    @Inject private WebsocketController websocketController;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String inputXml = ((TextMessage) message).getText();
                l.info("Text message received from MQ=" + C2Definitions.IBLOGGER_TO_C2_QUEUE + ", xml=" + inputXml);
                InputRequest inputRequest = C2Util.toInputRequest(inputXml);
                Calendar now = Calendar.getInstance();
                inputRequest.setReceivedDate(now);
                inputRequest.setStatus(C2Definitions.InputStatus.NEW);
                inputRequest.setStatusDate(now);
                c2Dao.newInputRequest(inputRequest);
                C2System c2System = c2Dao.getC2SystemByOriginAndSecType(inputRequest.getOrigin(), inputRequest.getSecType());
                if (c2System == null) {
                    l.warning("No C2 system for origin=" + inputRequest.getOrigin() + " and secType=" + inputRequest.getSecType() + ", skipping");
                    inputRequest.changeStatus(C2Definitions.InputStatus.IGNORED);
                    inputRequest.setIgnoreReason(C2Definitions.IgnoreReason.NOC2SYSTEM);
                    c2Dao.updateInputRequest(inputRequest);
                    return;
                }
                inputProcessor.process(c2System, inputRequest);
                websocketController.broadcastC2Message("input processed");
            } else {
                l.warning("Non-text message received from MQ=" + C2Definitions.IBLOGGER_TO_C2_QUEUE + ", ignoring");
            }
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}