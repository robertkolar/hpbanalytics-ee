package com.highpowerbear.hpbanalytics.c2.c2client;

/**
 *
 * @author Robert
 */
public class C2ApiEnums {
    public enum Action {
        BTO,
        SSHORT,
        STO,
        BTC,
        STC
    }

    public enum CancelResponse {
        COLLECTIVE2 ("collective2"),
        STATUS ("status"),
        ACK ("ack");

        private String name;

        CancelResponse(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Cmd {
        SIGNAL ("signal"),
        REVERSE ("reverse"),
        REQUESTOCAID ("requestocaid"),
        CANCEL ("cancel"),
        CANCELALLPENDING ("cancelallpending"),
        FLUSHPENDINGSIGNALS ("flushpendingsignals"),
        CLOSEALLPOSITIONS ("closeallpositions"),
        GETBUYPOWER ("getbuypower"),
        SIGNALSTATUS ("signalstatus"),
        POSITIONSTATUS ("positionstatus"),
        ALLSYSTEMS ("allsystems"),
        GETSYSTEMHYPO ("getsystemhypothetical"),
        GETALLSIGNALS ("getallsignals"),
        ADDTOOCAGROUP ("addtoocagroup"),
        NEWCOMMENT ("newcomment");

        private String name;

        Cmd(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum Duration {
        GTC ("GTC"),
        DAY ("DAY");

        private String name;

        Duration(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Duration getEnumFromName(String name) {
            if (name.equals(GTC.getName()))
                return GTC;
            if (name.equals(DAY.getName()))
                return DAY;
            return null;
        }
    }

    public enum GetAllSignalsResponse {
        COLLECTIVE2 ("collective2"),
        STATUS ("status"),
        ALLPENDINGSIGNALS ("allPendingSignals"),
        SYSTEM ("system"),
        SYSTEMID ("systemid"),
        PENDINGBLOCK ("pendingblock"),
        SIGNALID ("signalid");

        private String name;

        GetAllSignalsResponse(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Instrument {
        STOCK ("stock"),
        OPTION ("option"),
        FUTURE ("future"),
        FOREX ("forex");

        private String name;

        Instrument(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Instrument getEnumFromName(String name) {
            if (name.equals(STOCK.getName()))
                return STOCK;
            if (name.equals(OPTION.getName()))
                return OPTION;
            if (name.equals(FUTURE.getName()))
                return FUTURE;
            if (name.equals(FOREX.getName()))
                return FOREX;
            return null;
        }
    }

    public enum PositionStatusResponse {
        COLLECTIVE2 ("collective2"),
        STATUS ("status"),
        POSITIONSTATUS ("positionstatus"),
        CALCTIME ("calctime"),
        SYMBOL ("symbol"),
        POSITION ("position");

        private String name;

        PositionStatusResponse(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SignalResponse {
        COLLECTIVE2 ("collective2"),
        ACK ("ack"),
        STATUS ("status"),
        SIGNALID ("signalid"),
        COMMENTS ("comments"),
        OCA ("oca"),
        DELAY ("delay");

        private String name;

        SignalResponse(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SignalStatusResponse {
        COLLECTIVE2 ("collective2"),
        SIGNAL ("signal"),
        SIGNALID ("signalid"),
        SYSTEMNAME ("systemname"),
        POSTEDWHEN ("postedwhen"),
        EMAILEDWHEN ("emailedwhen"),
        KILLEDWHEN ("killedwhen"),
        EXPIREDWHEN ("expiredwhen"),
        TRADEDWHEN ("tradedwhen"),
        TRADEPRICE ("tradeprice"),

        // status element is present only in error response (C2 API not consistent), but will include it here
        STATUS ("status");

        private String name;

        SignalStatusResponse(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum UrlParam {
        CMD ("cmd"),
        SYSTEMID ("systemid"),
        PW ("pw"),
        ACTION ("action"),
        QUANT ("quant"),
        INSTRUMENT ("instrument"),
        SYMBOL ("symbol"),
        LIMIT ("limit"),
        STOP ("stop"),
        DURATION ("duration"),
        SIGNALID ("signalid"),
        CONDITIONALUPON ("conditionalupon"),
        XREPLACE("xreplace"),
        TRIGGERPRICE ("triggerprice"),
        OCAID ("ocaid"),
        STOPLOSS ("stoploss"),
        PROFITTARGET ("profittarget"),
        FORCENOOCA ("forcenooca"),
        MARKET ("market"),
        DELAY ("delay"),
        PARKUNTIL ("parkuntil"),
        CANCELSAT ("cancelsat"),
        CANCELSATRELATIVE ("cancelsatrelative"),
        PARKUNTILDATETIME ("parkuntildatetime"),
        C2EMAIL ("c2email"),
        SHOWRELATED ("showrelated"),
        SHOWDETAILS ("showdetails"),
        OCAGROUPID ("ocagroupid"),
        COMMENTARY ("commentary");

        private String name;

        UrlParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum XmlStatusContent {
        OK ("OK"),
        ERROR ("error");

        private String name;

        XmlStatusContent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
