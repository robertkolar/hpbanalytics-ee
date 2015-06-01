package com.highpowerbear.hpbanalytics.iblogger.common;

/**
 * Created by robertk on 3/30/15.
 */
public class IbLoggerDefinitions {
    // Constants
    public static final int ONE_SECOND = 1000; // milliseconds
    public static final String CONVERSION_ORIGIN_PREFIX_IB = "IB:";

    // Settings
    public static final String LOGGER = "com.highpowerbear.hpbsignals";
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
}