package com.highpowerbear.hpbanalytics.c2.common;

import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import org.w3c.dom.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rkolar on 1/30/14.
 */
public class C2Util {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    public static String getXmlString(Document dom) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(dom);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public static InputRequest toInputRequest(String xml) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(InputRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader stringReader = new StringReader(xml);
        return (InputRequest) jaxbUnmarshaller.unmarshal(stringReader);
    }

    public static void sleepMillis(Long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            l.log(Level.SEVERE, "Error, e");
        }
    }
}