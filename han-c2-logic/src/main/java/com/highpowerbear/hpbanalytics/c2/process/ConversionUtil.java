package com.highpowerbear.hpbanalytics.c2.process;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert
 */
public class ConversionUtil {
    private static final Map<String, String> optionsConvTable = new HashMap<>();
    static {
        optionsConvTable.put("01C", "A");
        optionsConvTable.put("02C", "B");
        optionsConvTable.put("03C", "C");
        optionsConvTable.put("04C", "D");
        optionsConvTable.put("05C", "E");
        optionsConvTable.put("06C", "F");
        optionsConvTable.put("07C", "G");
        optionsConvTable.put("08C", "H");
        optionsConvTable.put("09C", "I");
        optionsConvTable.put("10C", "J");
        optionsConvTable.put("11C", "K");
        optionsConvTable.put("12C", "L");
        optionsConvTable.put("01P", "M");
        optionsConvTable.put("02P", "N");
        optionsConvTable.put("03P", "O");
        optionsConvTable.put("04P", "P");
        optionsConvTable.put("05P", "Q");
        optionsConvTable.put("06P", "R");
        optionsConvTable.put("07P", "S");
        optionsConvTable.put("08P", "T");
        optionsConvTable.put("09P", "U");
        optionsConvTable.put("10P", "V");
        optionsConvTable.put("11P", "W");
        optionsConvTable.put("12P", "X");
    }
    
    private static final Map<String, String> futuresConvTable = new HashMap<>();
    static {
        futuresConvTable.put("ES", "@ES");
        futuresConvTable.put("NQ", "@NQ");
        futuresConvTable.put("YM", "@YM");
        futuresConvTable.put("TF", "@TFS");
        futuresConvTable.put("SPX", "@SP");
        futuresConvTable.put("NDX", "@ND");
        futuresConvTable.put("DX", "@DX");
        futuresConvTable.put("GC", "QGC");
        futuresConvTable.put("SI", "QSI");
        futuresConvTable.put("HG", "QHG");
        futuresConvTable.put("PA", "QPA");
        futuresConvTable.put("PL", "QPL");
        futuresConvTable.put("ZG", "@ZG");
        futuresConvTable.put("YG", "@YG");
        futuresConvTable.put("ZI", "@ZI");
        futuresConvTable.put("YI", "@YI");
        futuresConvTable.put("CL", "QCL");
        futuresConvTable.put("QM", "@QM");
        futuresConvTable.put("NG", "QNG");
        futuresConvTable.put("HO", "QHO");
        futuresConvTable.put("RB", "QRB");
        futuresConvTable.put("ZC", "@C");
        futuresConvTable.put("ZS", "@S");
        futuresConvTable.put("ZM", "@SM");
        futuresConvTable.put("ZL", "@BO");
        futuresConvTable.put("ZW", "@W");
        futuresConvTable.put("ZO", "@O");
        futuresConvTable.put("ZR", "@RR");
        futuresConvTable.put("ZB", "@US");
        futuresConvTable.put("ZN", "@TY");
        futuresConvTable.put("ZF", "@FV");
        futuresConvTable.put("ZT", "@TU");
        futuresConvTable.put("GE", "@ED");
        futuresConvTable.put("CC", "@CC");
        futuresConvTable.put("KC", "@KC");
        futuresConvTable.put("CT", "@CT");
        futuresConvTable.put("OJ", "@OJ");
        futuresConvTable.put("GF", "@GF");
        futuresConvTable.put("LE", "@LE");
        futuresConvTable.put("HE", "@HE");
        futuresConvTable.put("6E", "@EU");
        futuresConvTable.put("E7", "@ME");
        futuresConvTable.put("M6E", "@M6E");
        futuresConvTable.put("6A", "@AD");
        futuresConvTable.put("M6A", "@M6A");
        futuresConvTable.put("6B", "@BP");
        futuresConvTable.put("M6B", "@M6B");
        futuresConvTable.put("6C", "@CD");
        futuresConvTable.put("MCD", "@MCD");
        futuresConvTable.put("6S", "@SF");
        futuresConvTable.put("MSF", "@MSF");
        futuresConvTable.put("6J", "@JY");
        futuresConvTable.put("J7", "@JE");
        futuresConvTable.put("M6J", "@M6J");
        futuresConvTable.put("6M", "@PX");
        futuresConvTable.put("6N", "@NE");
        futuresConvTable.put("6Z", "@RA");
    }
    
    private static final Map<String, String> futuresMonthConvTable = new HashMap<>();
    static {
        futuresMonthConvTable.put("JAN", "F");
        futuresMonthConvTable.put("FEB", "G");
        futuresMonthConvTable.put("MAR", "H");
        futuresMonthConvTable.put("APR", "J");
        futuresMonthConvTable.put("MAY", "K");
        futuresMonthConvTable.put("JUN", "M");
        futuresMonthConvTable.put("JUL", "N");
        futuresMonthConvTable.put("AUG", "Q");
        futuresMonthConvTable.put("SEP", "U");
        futuresMonthConvTable.put("OCT", "V");
        futuresMonthConvTable.put("NOV", "X");
        futuresMonthConvTable.put("DEC", "Z");
    }

    public static String convertOptionSymbol(String ibOptionSymbol) {
        // Convert from standard format used by IB to nonstandard format used by C2
        // 7 must be ommited because CBOE mini options trading classes like AAPL7, years 207x won't be parsed correctly, but who cares :)
        String root = ibOptionSymbol.split("[0-68-9]")[0];
        String s = ibOptionSymbol.substring(root.length());
        root = root.trim();
        String year = s.substring(0, 2);
        String month = s.substring(2, 4);
        String day = s.substring(4, 6);
        String type = s.substring(6, 7);
        String strikeWhole = s.substring(7, 12).replaceFirst("[0]+", "");
        String strikeDigit = s.substring(12, 15).replaceFirst("[0]+", "");

        String monthType = optionsConvTable.get(month + type);

        return root + year + day + monthType + strikeWhole + (strikeDigit.length() > 0 ? "." + strikeDigit : "");
    }

    public static String convertFutureSymbol(String ibFutureSymbol) {
        String monthYear;
        String ibRoot;
        if (ibFutureSymbol.length() < 7) {
            // extract last two characters
            monthYear = ibFutureSymbol.substring(ibFutureSymbol.length() - 2);
            ibRoot = ibFutureSymbol.substring(0, ibFutureSymbol.length() - 2);
        } else {
            monthYear = futuresMonthConvTable.get(ibFutureSymbol.substring(2, 5).toUpperCase()) + ibFutureSymbol.substring(6, 7);
            ibRoot = ibFutureSymbol.substring(0, 2);
        }
        // NQM0 --> @NQM0
        String c2Root = futuresConvTable.get(ibRoot);
        return c2Root + monthYear;
    }

    public static String convertForexSymbol(String ibForexSymbol) {
        // EUR.USD --> EURUSD
        return ibForexSymbol.replace(".", "");
    }

    public static Integer convertForexQuant(Integer ibForexQuant) {
        return ibForexQuant / C2Definitions.ONE_MINILOT_FOREX_QUANT;
    }
}
