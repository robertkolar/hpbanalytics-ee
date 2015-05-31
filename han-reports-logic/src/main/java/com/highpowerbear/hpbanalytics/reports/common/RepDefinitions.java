package com.highpowerbear.hpbanalytics.reports.common;

/**
 *
 * @author Robert
 */
public class RepDefinitions {
    // settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static final Integer RECENT_EXECUTIONS_LIMIT = 20;

    // constants
    public static final String ORIGIN_MANUAL = "manual";
    public static final String NA = "N/A";

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

        public static Action getEnumFromName(String name) {
            if (name.equals(BUY.getName())) {
                return BUY;
            } else if (name.equals(SELL.getName())) {
                return SELL;
            } else {
                return null;
            }
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

        public static SecType getEnumFromName(String name) {
            if (name.equals(STK.getName())) {
                return STK;
            } else if (name.equals(OPT.getName())) {
                return OPT;
            } else if (name.equals(FUT.getName())) {
                return FUT;
            } else if (name.equals(CASH.getName())) {
                return CASH;
            } else {
                return null;
            }
        }
    }

    public enum Currency {
        USD,
        EUR,
        AUD,
        GBP,
        CHF,
        CAD,
        JPY;

        public static Currency getEnumFromName(String name) {
            if (USD.name().equalsIgnoreCase(name)) {
                return USD;
            } else if (EUR.name().equalsIgnoreCase(name)) {
                return EUR;
            } else if (AUD.name().equalsIgnoreCase(name)) {
                return AUD;
            } else if (GBP.name().equalsIgnoreCase(name)) {
                return GBP;
            } else if (CHF.name().equalsIgnoreCase(name)) {
                return CHF;
            } else if (CAD.name().equalsIgnoreCase(name)) {
                return CAD;
            } else if (JPY.name().equalsIgnoreCase(name)) {
                return JPY;
            } else {
                return null;
            }
        }
    }
}
