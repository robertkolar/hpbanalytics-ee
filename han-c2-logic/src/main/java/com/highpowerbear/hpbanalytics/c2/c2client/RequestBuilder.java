package com.highpowerbear.hpbanalytics.c2.c2client;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@ApplicationScoped
public class RequestBuilder {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    public String buildCancelRequest(C2Signal c2Signal) {
        return  (c2Signal.getC2System().isUseSsl() ? "https://" : "http://") + C2Definitions.C2_BASE_URL + "?" +
                C2ApiEnums.UrlParam.CMD.getName() + "=" + C2ApiEnums.Cmd.CANCEL.getName() + "&" +
                C2ApiEnums.UrlParam.SIGNALID.getName() + "=" + c2Signal.getC2SignalId() + "&" +
                C2ApiEnums.UrlParam.SYSTEMID.getName() + "=" + c2Signal.getC2System().getSystemId() + "&" +
                C2ApiEnums.UrlParam.PW.getName() + "=" + c2Signal.getC2System().getPassword();
    }

    public String buildPositionStatusRequest(C2Signal c2Signal) {
        return  (c2Signal.getC2System().isUseSsl() ? "https://" : "http://") + C2Definitions.C2_BASE_URL + "?" +
                C2ApiEnums.UrlParam.CMD.getName() + "=" + C2ApiEnums.Cmd.POSITIONSTATUS.getName() + "&" +
                C2ApiEnums.UrlParam.PW.getName() + "=" + c2Signal.getC2System().getPassword() + "&" +
                C2ApiEnums.UrlParam.SYSTEMID.getName() + "=" + c2Signal.getC2System().getSystemId() + "&" +
                C2ApiEnums.UrlParam.SYMBOL.getName() + "=" + c2Signal.getSymbol();
    }

    public String buildSignalRequest(C2Signal c2Signal) {
        return  (c2Signal.getC2System().isUseSsl() ? "https://" : "http://") + C2Definitions.C2_BASE_URL + "?" +
                C2ApiEnums.UrlParam.CMD.getName() + "=" + C2ApiEnums.Cmd.SIGNAL.getName() +
                "&" + C2ApiEnums.UrlParam.SYSTEMID.getName() + "=" + c2Signal.getC2System().getSystemId() +
                "&" + C2ApiEnums.UrlParam.PW.getName() + "=" + c2Signal.getC2System().getPassword() +
                "&" + C2ApiEnums.UrlParam.INSTRUMENT.getName() + "=" + c2Signal.getInstrument().getName() +
                "&" + C2ApiEnums.UrlParam.ACTION.getName() + "=" + c2Signal.getAction().name() +
                "&" + C2ApiEnums.UrlParam.QUANT.getName() + "=" + c2Signal.getQuant() +
                "&" + C2ApiEnums.UrlParam.SYMBOL.getName() + "=" + c2Signal.getSymbol() +
                (c2Signal.getLimitPrice() != 0.0 ? "&" + C2ApiEnums.UrlParam.LIMIT.getName() + "=" + c2Signal.getLimitPrice() : "") +
                (c2Signal.getStopPrice() != 0.0 ? "&" + C2ApiEnums.UrlParam.STOP.getName() + "=" + c2Signal.getStopPrice() : "") +
                "&" + C2ApiEnums.UrlParam.DURATION.getName() + "=" + c2Signal.getDuration().getName() +
                (c2Signal.getOcaGroup() != null ? "&" + C2ApiEnums.UrlParam.OCAID.getName() + "=" + c2Signal.getOcaGroup() : "") +
                (c2Signal.getReversalParent() != null ? "&" + C2ApiEnums.UrlParam.CONDITIONALUPON.getName() + "=" + c2Signal.getReversalParent() : "");
    }

    public String buildSignalStatusRequest(C2Signal c2Signal) {
        return  (c2Signal.getC2System().isUseSsl() ? "https://" : "http://") + C2Definitions.C2_BASE_URL + "?" +
                C2ApiEnums.UrlParam.CMD.getName() + "=" + C2ApiEnums.Cmd.SIGNALSTATUS.getName() + "&" +
                C2ApiEnums.UrlParam.SIGNALID.getName() + "=" + c2Signal.getC2SignalId() + "&" +
                C2ApiEnums.UrlParam.PW.getName() + "=" + c2Signal.getC2System().getPassword() + "&" +
                C2ApiEnums.UrlParam.C2EMAIL.getName() + "=" + c2Signal.getC2System().getEmail();
    }
}
