package com.highpowerbear.hpbanalytics.c2.common;

/**
 * Created by robertk on 3/30/15.
 */
public class C2Definitions {
    // Constants
    public static final Integer ONE_MINILOT_FOREX_QUANT = 10000;

    // Settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static int C2_STATUS_POLLING_MAX_FAILED = 10;
    public static final String C2_BASE_URL = "www.collective2.com/cgi-perl/signal.mpl";
    public static final int JPA_MAX_RESULTS = 1000;
    public static final long REQUESTS_WITH_OCAGROUP_DELAY = 2000; // millis

    // Enums
    public enum RequestType {
        SUBMIT,
        CANCEL,
        UPDATE
    }

    public enum InputStatus {
        NEW,
        PROCESSED,
        IGNORED,
        ERROR
    }

    public enum PublishStatus {
        POSOK,
        POSERR,
        SBMOK,
        SBMERR,
        CNCOK,
        CNCERR,
        UPDCNCOK,
        UPDCNCERR,
        UPDSBMOK,
        UPDSBMERR
    }

    public enum PollStatus {
        NOTPOLLED,
        WORKING,
        CANCELLED,
        FILLED,
        EXPIRED,
        POLLERR,
        UNKNOWN
    }

    public enum IgnoreReason {
        NOWORKINGSIGNAL,
        REVERSALUPDATE,
        NOC2SYSTEM,
        OCAGROUPSET
    }

    public enum ReversalSignalType {
        NONE,
        PARENT,
        CHILD
    }

    public enum Action {
        BUY,
        SELL
    }

    public enum OrderType {
        MKT,
        LMT,
        STP
    }

    public enum SecType {
        STK,
        OPT,
        FUT,
        CASH
    }

    public enum Tif {
        DAY,
        GTC
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

    public enum InputRequestFilterField {
        RECEIVED_DATE("receivedDate"),
        STATUS("status"),
        SYMBOL("symbol"),
        SEC_TYPE("secType");

        private String varName;

        InputRequestFilterField(String varName) {
            this.varName = varName;
        }

        public String getVarName() {
            return varName;
        }
    }

    public enum C2SignalFilterField {
        CREATED_DATE("createdDate"),
        PUBLISH_STATUS("publishStatus"),
        POLL_STATUS("pollStatus"),
        SYMBOL("symbol");

        private String varName;

        C2SignalFilterField(String varName) {
            this.varName = varName;
        }

        public String getVarName() {
            return varName;
        }
    }
}
