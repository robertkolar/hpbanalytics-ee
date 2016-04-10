package com.highpowerbear.hpbanalytics.report.message;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.common.ReportUtil;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;
import com.highpowerbear.hpbanalytics.report.process.ReportProcessor;

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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/iblogger_report"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
})
public class MqListenerBean implements MessageListener {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private ReportDao reportDao;
    @Inject private ReportProcessor reportProcessor;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String inputXml = ((TextMessage) message).getText();
                l.info("Text message received from MQ=iblogger_report, xml=" + inputXml);
                Execution execution = ReportUtil.toExecution(inputXml);
                Calendar now = Calendar.getInstance();
                execution.setReceivedDate(now);
                Report report = reportDao.getReportByOriginAndSecType(execution.getOrigin(), execution.getSecType());
                if (report == null) {
                    l.warning("No report for origin=" + execution.getOrigin() + " and secType=" + execution.getSecType() + ", skipping");
                    return;
                }
                execution.setReport(report);
                reportProcessor.newExecution(execution);
            } else {
                l.warning("Non-text message received from MQ=iblogger_report, ignoring");
            }
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}