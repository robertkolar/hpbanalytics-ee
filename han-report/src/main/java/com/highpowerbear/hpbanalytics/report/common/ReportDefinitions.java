package com.highpowerbear.hpbanalytics.report.common;

/**
 *
 * @author Robert
 */
public class ReportDefinitions {
    // settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static final String IBLOGGER_TO_REPORT_QUEUE = "java:/jms/queue/IbLoggerToReportQ";
    public static final int JPA_MAX_RESULTS = 1000;
    public static final String TIMEZONE = "America/New_York";
    public static final String EXCHANGE_RATE_URL = "http://api.fixer.io/";
    public static final Integer EXCHANGE_RATE_DAYS_BACK = 5;

    // constants
    public static final String ORIGIN_INTERNAL = "INTERNAL";
    public static final String NOT_AVAILABLE = "N/A";
    public static final String ALL_UNDERLYINGS = "ALLUNDLS";
    public static final String ASSIGN_TRADE_COMMENT = "ASSIGN";
    public static final String EXPIRE_TRADE_COMMENT = "EXPIRE";
    public static final String CLOSE_TRADE_COMMENT = "CLOSE";

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

    public enum FilterOperatorString {
        LIKE("LIKE");

        private String sql;
        FilterOperatorString(String sql) {
            this.sql = sql;
        }
        public String getSql() {
            return sql;
        }
    }

    public enum FilterOperatorNumber {
        EQ("="),
        GT(">"),
        LT("<");

        private String sql;
        FilterOperatorNumber(String sql) {
            this.sql = sql;
        }
        public String getSql() {
            return sql;
        }
    }

    public enum FilterOperatorCalendar {
        EQ("="),
        GT(">"),
        LT("<");

        private String sql;
        FilterOperatorCalendar(String sql) {
            this.sql = sql;
        }
        public String getSql() {
            return sql;
        }
    }

    public enum FilterOperatorEnum {
        IN("IN");

        private String sql;
        FilterOperatorEnum(String sql) {
            this.sql = sql;
        }
        public String getSql() {
            return sql;
        }
    }

    public enum FilterKey {
        PROPERTY,
        OPERATOR,
        VALUE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public enum ExecutionFilterField {
        SYMBOL("symbol"),
        SEC_TYPE("secType"),
        FILL_DATE("fillDate");

        private String varName;

        ExecutionFilterField(String varName) {
            this.varName = varName;
        }

        public String getVarName() {
            return varName;
        }
    }

    public enum TradeFilterField {
        SYMBOL("symbol"),
        SEC_TYPE("secType"),
        OPEN_DATE("openDate"),
        STATUS("status");

        private String varName;

        TradeFilterField(String varName) {
            this.varName = varName;
        }

        public String getVarName() {
            return varName;
        }
    }
}
