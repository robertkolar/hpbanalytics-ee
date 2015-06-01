package com.highpowerbear.hpbanalytics.c2.common;

/**
 * Created by robertk on 3/30/15.
 */
public class C2Definitions {
    // Constants
    public static final Integer ONE_MINILOT_FOREX_QUANT = 10000;
    public static final String CONVERSION_ORIGIN_PREFIX_IB = "IB:";

    // Settings
    public static final String LOGGER = "com.highpowerbear.hpbanalytics";
    public static final Integer C2_STATUS_POLLING_MAX_FAILED = 10;
    public static final String C2_BASE_URL = "www.collective2.com/cgi-perl/signal.mpl";
    public static final Integer JPA_MAX_RESULTS = 1000;

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
        NOWRKSIG,
        REVUPD
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
}
