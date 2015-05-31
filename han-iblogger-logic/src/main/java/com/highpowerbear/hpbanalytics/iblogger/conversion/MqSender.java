package com.highpowerbear.hpbanalytics.iblogger.conversion;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
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
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = "java:/jms/queue/iblogtoc2pub")
    private Queue iblogToC2pubQ;
    @Resource(lookup = "java:/jms/queue/iblogtoanalytics")
    private Queue iblogToAnalyticsQ;

    public void sendToC2pub(String requestXml) {
        l.info("BEGIN send message to MQ=iblogToC2pubQ, xml=" + requestXml);
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage(requestXml);
        producer.send(iblogToC2pubQ, message);
        l.info("END send message to MQ=iblogToC2pubQ");
    }

    public void sendToAnalytics(String executionXml) {
        l.info("BEGIN send message to MQ=iblogToAnalyticsQ, xml=" + executionXml);
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage(executionXml);
        producer.send(iblogToAnalyticsQ, message);
        l.info("END send message to MQ=iblogToAnalyticsQ");
    }
}