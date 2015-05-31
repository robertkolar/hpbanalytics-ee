package com.highpowerbear.hpbanalytics.c2.conversion;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.common.C2Util;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/iblogtoc2pub"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MqListenerBean implements MessageListener {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @Inject private C2Dao c2Dao;
    @Inject private InputProcessor inputProcessor;


    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String inputXml = ((TextMessage) message).getText();
                l.info("Text message received from MQ=iblogtoc2pub, xml=" + inputXml);
                InputRequest inputRequest = C2Util.toInputRequest(inputXml);
                Calendar now = Calendar.getInstance();
                inputRequest.setReceivedDate(now);
                inputRequest.setStatus(C2Definitions.InputStatus.NEW);
                inputRequest.setStatusDate(now);
                c2Dao.newInputRequest(inputRequest);
                C2System c2System = c2Dao.getC2SystemByConversionOrigin(inputRequest.getOrigin());
                inputProcessor.process(c2System, inputRequest);
            } else {
                l.warning("Non-text message received from MQ=iblogtoc2pub, ignoring");
            }
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}