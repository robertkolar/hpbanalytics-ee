package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.common.RepUtil;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;

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
 * Created by robertk on 4/26/15.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/iblogger_reports"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MqListenerBean implements MessageListener {
    private static final Logger l = Logger.getLogger(RepDefinitions.LOGGER);

    @Inject private RepDao repDao;
    @Inject private RepProcessor repProcessor;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String inputXml = ((TextMessage) message).getText();
                l.info("Text message received from MQ=iblogger_reports, xml=" + inputXml);
                Execution execution = RepUtil.toExecution(inputXml);
                Calendar now = Calendar.getInstance();
                execution.setReceivedDate(now);
                Report report = repDao.getReportByOrigin(execution.getOrigin());
                if (report == null) {
                    l.warning("No analytics for origin=" + execution.getOrigin() + ", skipping");
                    return;
                }
                execution.setReport(report);
                repProcessor.newExecution(execution);
            } else {
                l.warning("Non-text message received from MQ=iblogger_reports, ignoring");
            }
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}