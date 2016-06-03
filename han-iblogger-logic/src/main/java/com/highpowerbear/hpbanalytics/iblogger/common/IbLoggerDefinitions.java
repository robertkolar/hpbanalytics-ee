package com.highpowerbear.hpbanalytics.iblogger.common;

/**
 * Created by robertk on 3/30/15.
 */
public class IbLoggerDefinitions {
    // Constants
    public static final int ONE_SECOND = 1000; // milliseconds
    public static final String CONVERSION_ORIGIN_PREFIX_IB = "IB:";

    // Settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static final String IBLOGGER_TO_C2_QUEUE = "java:/jms/queue/IbLoggerToC2Q";
    public static final String IBLOGGER_TO_REPORT_QUEUE = "java:/jms/queue/IbLoggerToReportQ";
    public static final Integer IB_CONNECT_CLIENT_ID = 0;
    public static final Integer JPA_MAX_RESULTS = 1000;
    public static final Integer MAX_ORDER_HEARTBEAT_FAILS = 5;

    // Enums
    public enum IbOrderStatus {
        SUBMITTED,
        UPDATED,
        CANCELLED,
        FILLED,
        UNKNOWN
    }

    public enum RequestType {
        SUBMIT,
        CANCEL,
        UPDATE
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

    public enum IbOrderFilterField {
        SYMBOL("symbol"),
        SEC_TYPE("secType"),
        SUBMIT_DATE("submitDate"),
        STATUS("status");

        private String varName;

        IbOrderFilterField(String varName) {
            this.varName = varName;
        }

        public String getVarName() {
            return varName;
        }

    }
}