package com.highpowerbear.hpbanalytics.iblogger.ibclient;

/**
 *
 * @author Robert
 */
public class IbApiEnums {
    public enum Action {
        BUY,
        SELL,
        SSHORT;

        public static Action getEnumFromName(String name) {
            if (BUY.name().equalsIgnoreCase(name)) {
                return BUY;
            } else if (SELL.name().equalsIgnoreCase(name)) {
                return SELL;
            } else if (SSHORT.name().equalsIgnoreCase(name)) {
                return SSHORT;
            } else {
                return null;
            }
        }
    }

    public enum OrderStatus {
        PENDINGSUBMIT ("PendingSubmit"),
        PENDINGCANCEL ("PendingCancel"),
        PRESUBMITTED ("PreSubmitted"),
        SUBMITTED ("Submitted"),
        CANCELLED ("Cancelled"),
        FILLED ("Filled"),
        INACTIVE ("Inactive");

        private String name;

        OrderStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
        TRAILLIMIT;

        public static OrderType getEnumFromName(String name) {
            if (MKT.name().equalsIgnoreCase(name)) {
                return MKT;
            } else if (LMT.name().equalsIgnoreCase(name)) {
                return LMT;
            } else if (STP.name().equalsIgnoreCase(name)) {
                return STP;
            } else {
                return null;
            }
        }
    }

    public enum SecType {
        STK,
        OPT,
        FUT,
        IND,
        FOP,
        CASH,
        BAG;

        public static SecType getEnumFromName(String name) {
            if (STK.name().equalsIgnoreCase(name)) {
                return STK;
            } else if (OPT.name().equalsIgnoreCase(name)) {
                return OPT;
            } else if (FUT.name().equalsIgnoreCase(name)) {
                return FUT;
            } else if (CASH.name().equalsIgnoreCase(name)) {
                return CASH;
            } else {
                return null;
            }
        }
    }

    public enum Tif {
        DAY,
        GTC,
        IOC,
        GTD;

        public static Tif getEnumFromName(String name) {
            if (DAY.name().equalsIgnoreCase(name)) {
                return DAY;
            } else if (GTC.name().equalsIgnoreCase(name)) {
                return GTC;
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
