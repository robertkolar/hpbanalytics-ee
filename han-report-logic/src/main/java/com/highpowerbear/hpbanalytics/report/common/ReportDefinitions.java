package com.highpowerbear.hpbanalytics.report.common;

/**
 *
 * @author Robert
 */
public class ReportDefinitions {
    // settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static final Integer RECENT_EXECUTIONS_LIMIT = 20;
    public static final int JPA_MAX_RESULTS = 1000;

    // constants
    public static final String ORIGIN_MANUAL = "manual";
    public static final String NA = "N/A";
    public static final String ALL_UNDERLYINGS = "ALLUNDLS";

    // enums
    public enum OptionType {
        CALL("CALL", "C"),
        PUT("PUT", "P");
        
        private String name;
        private String shortName;
        OptionType(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }
        public String getName() {
            return name;
        }
        public String getShortName() {
            return shortName;
        }
        public static OptionType getFromShortName(String shortName) {
            return (shortName == null ? null : (shortName.equalsIgnoreCase(PUT.getShortName()) ? PUT : (shortName.equalsIgnoreCase(CALL.getShortName()) ? CALL : null)));
        }
    }
    
    public enum OptionCloseType {
        CLOSE, 
        ASSIGN, 
        EXPIRE, 
        EXERCISE
    }

    public enum TradeType {
        LONG,
        SHORT
    }
    
    public enum TradeStatus {
        OPEN,
        CLOSED
    }
    
    public enum FuturePlMultiplier {
        ES("ES", 50),
        NQ("NQ", 20),
        YM("YM", 5);
        
        private String underlying;
        private Integer multiplier;
        
        FuturePlMultiplier(String underlying, Integer multiplier) {
            this.underlying = underlying;
            this.multiplier = multiplier;
        }
        public static Integer getMultiplierByUnderlying(String underlying) {
            if (ES.underlying.equalsIgnoreCase(underlying)) return ES.multiplier;
            if (NQ.underlying.equalsIgnoreCase(underlying)) return NQ.multiplier;
            if (YM.underlying.equalsIgnoreCase(underlying)) return YM.multiplier;
            return 1;
        }
    }
    
    public enum StatisticsInterval {
        DAY("Daily"),
        MONTH("Monthly");
        
        private String name;
        StatisticsInterval(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public static StatisticsInterval getByName(String name) {
            return (DAY.getName().equals(name) ? DAY : MONTH);
        }
    }

    public enum Action {
        BUY ("BUY"),
        SELL ("SELL");

        private String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SecType {
        STK ("STK"),
        OPT ("OPT"),
        FUT ("FUT"),
        CASH ("CASH");

        private String name;

        SecType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Currency {
        USD,
        EUR,
        AUD,
        GBP,
        CHF,
        CAD,
        JPY
    }
}
