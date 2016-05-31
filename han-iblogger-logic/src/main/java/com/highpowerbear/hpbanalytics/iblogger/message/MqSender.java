package com.highpowerbear.hpbanalytics.iblogger.message;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = "java:/jms/queue/IbLoggerToC2Q")
    private Queue ibLoggerToC2Q;
    @Resource(lookup = "java:/jms/queue/IbLoggerToReportQ")
    private Queue ibLoggerToReportQ;

    public void sendToC2(String requestXml) {
        l.info("BEGIN send message to MQ=IbLoggerToC2Q, xml=" + requestXml);
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage(requestXml);
        producer.send(ibLoggerToC2Q, message);
        l.info("END send message to MQ=IbLoggerToC2Q");
    }

    public void sendToReport(String executionXml) {
        l.info("BEGIN send message to MQ=IbLoggerToReportQ, xml=" + executionXml);
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage(executionXml);
        producer.send(ibLoggerToReportQ, message);
        l.info("END send message to MQ=IbLoggerToReportQ");
    }
}