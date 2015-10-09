package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.model.OptionParseResult;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
public class OptionUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ReportDefinitions.LOGGER);

    private static final List<String> miniOptionRoots = new ArrayList<>();
    static {
        miniOptionRoots.add("AMZN7");
        miniOptionRoots.add("AAPL7");
        miniOptionRoots.add("GOOG7");
        miniOptionRoots.add("GLD7");
        miniOptionRoots.add("SPY7");
    }
    
    public static boolean isMini(String optionSymbol) {
        boolean isMini = false;
        for (String miniOptionRoot : miniOptionRoots) {
            if (optionSymbol.startsWith(miniOptionRoot)) {
                isMini = true;
                break;
            }
        }
        return isMini;
    }
    
    public static OptionParseResult parse(String oSymbol) throws Exception {
        OptionParseResult result = new OptionParseResult();
        if (oSymbol.length() > 21 || oSymbol.length() < 16) {
            throw new Exception(oSymbol + " has not correct length");
        }
        int l = oSymbol.length();
        result.setUnderlying(oSymbol.substring(0, (isMini(oSymbol) ? l-16 : l-15)).trim().toUpperCase());

        String yy = oSymbol.substring(l-15, l-13);
        String MM = oSymbol.substring(l-13, l-11);
        String dd = oSymbol.substring(l-11, l-9);
        result.setOptType(ReportDefinitions.OptionType.getFromShortName(oSymbol.substring(l - 9, l - 8)));

        String str = oSymbol.substring(l-8, l-3);
        String strDec = oSymbol.substring(l-3, l);
        DateFormat df = new SimpleDateFormat("yyMMdd");
        df.setLenient(false);
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date expDate = df.parse(yy+MM+dd);
        expDate.setTime(expDate.getTime() + (1000 * 60 * 60 * 23)); // add 23 hours
        result.setExpDate(expDate);

        DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String ds = df1.format(expDate);
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        result.setStrikePrice(nf.parse(str + "." + strDec).doubleValue());

        logger.info("Parsed: " + oSymbol + " --> " + result.getUnderlying() + " " + result.getOptType() + " " + ds + " " + result.getStrikePrice());

        return result;
    }
}
