package com.highpowerbear.hpbanalytics.iblogger.ibclient;

/**
 *
 * @author Robert
 */
public class IbApiEnums {
    public enum Action {
        BUY,
        SELL,
        SSHORT
    }

    public enum OrderStatus {
        PENDINGSUBMIT ("PendingSubmit"),
        PENDINGCANCEL ("PendingCancel"),
        PRESUBMITTED ("PreSubmitted"),
        SUBMITTED ("Submitted"),
        CANCELLED ("Cancelled"),
        FILLED ("Filled"),
        INACTIVE ("Inactive");

        private String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum OrderType {
        MKT,
        MKTCLS,
        LMT,
        LMTCLS,
        PEGMKT,
        SCALE ,
        STP,
        STPLMT,
        TRAIL,
        REL,
        VWAP,
        TRAILLIMIT
    }

    public enum SecType {
        STK,
        OPT,
        FUT,
        IND,
        FOP,
        CASH,
        BAG,
        CFD
    }

    public enum Tif {
        DAY,
        GTC,
        IOC,
        GTD
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
