package com.highpowerbear.hpbanalytics.report.common;

import com.highpowerbear.hpbanalytics.report.entity.Execution;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author robertk
 */
public class ReportUtil {
    public static Calendar toBeginOfPeriod(Calendar cal, ReportDefinitions.StatisticsInterval interval) {
        Calendar beginPeriodDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        beginPeriodDate.setTimeInMillis(cal.getTimeInMillis());
        if (ReportDefinitions.StatisticsInterval.YEAR.equals(interval)) {
            beginPeriodDate.set(Calendar.MONTH, 0);
            beginPeriodDate.set(Calendar.DAY_OF_MONTH, 1);

        } else if (ReportDefinitions.StatisticsInterval.MONTH.equals(interval)) {
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
    public static double round(double number, int decimalPlaces) {
        double modifier = Math.pow(10.0, decimalPlaces);
        return Math.round(number * modifier) / modifier;
    }

    public static double round2(double number) {
        return round(number, 2);
    }

}
