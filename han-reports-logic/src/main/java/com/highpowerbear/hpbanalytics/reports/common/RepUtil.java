package com.highpowerbear.hpbanalytics.reports.common;

import com.highpowerbear.hpbanalytics.reports.entity.Execution;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author robertk
 */
public class RepUtil {
    public static Calendar toBeginOfPeriod(Calendar cal, RepDefinitions.StatisticsInterval interval) {
        Calendar beginPeriodDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        beginPeriodDate.setTimeInMillis(cal.getTimeInMillis());
        if (RepDefinitions.StatisticsInterval.MONTH.equals(interval)) {
            beginPeriodDate.set(Calendar.DAY_OF_MONTH, 1);
        }
        beginPeriodDate.set(Calendar.HOUR_OF_DAY, 0);
        beginPeriodDate.set(Calendar.MINUTE, 0);
        beginPeriodDate.set(Calendar.SECOND, 0);
        beginPeriodDate.set(Calendar.MILLISECOND, 0);
        return beginPeriodDate;
    }
    
    public static String toDurationString(long millis) {
        long days = millis / (24 * 60 * 60 * 1000);
        long daysRemainder = millis % (24 * 60 * 60 * 1000);
        long hours = daysRemainder / (60 * 60 * 1000);
        long hoursRemainder = daysRemainder % (60 * 60 * 1000);
        long minutes = hoursRemainder / (60 * 1000);
        long minutesRemainder = hoursRemainder % (60 * 1000);
        long seconds = minutesRemainder / (1000);
        return days + " Days " + hours + ":" + minutes + ":" + seconds;
    }

    public static Execution toExecution(String xml) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Execution.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader stringReader = new StringReader(xml);
        return (Execution) jaxbUnmarshaller.unmarshal(stringReader);
    }
}
