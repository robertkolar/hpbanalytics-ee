package com.highpowerbear.hpbanalytics.iblogger.common;

import com.highpowerbear.hpbanalytics.iblogger.model.C2Request;
import com.highpowerbear.hpbanalytics.iblogger.model.IbExecution;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Created by robertk on 3/28/15.
 */
public class IbLoggerUtil {
    public static void waitMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            // Ignore
        }
    }

    public static String removeSpace(String source) {
        return source.replaceAll("\\b\\s+\\b", "");
    }

    public static String toXml(C2Request c2Request) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(C2Request.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter stringWriter = new StringWriter();
        jaxbMarshaller.marshal(c2Request, stringWriter);
        return stringWriter.toString();
    }

    public static String toXml(IbExecution ibExecution) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(IbExecution.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter stringWriter = new StringWriter();
        jaxbMarshaller.marshal(ibExecution, stringWriter);
        return stringWriter.toString();
    }
}
