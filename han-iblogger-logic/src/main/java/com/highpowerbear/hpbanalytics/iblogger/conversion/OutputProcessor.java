package com.highpowerbear.hpbanalytics.iblogger.conversion;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbApiEnums;
import com.highpowerbear.hpbanalytics.iblogger.model.C2Request;
import com.highpowerbear.hpbanalytics.iblogger.model.IbExecution;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbloggerDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/29/15.
 */
@Named
@ApplicationScoped
public class OutputProcessor {
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Inject private IbloggerDao ibloggerDao;
    @Inject private MqSender mqSender;

    public void processConversion(IbOrder ibOrder, IbloggerDefinitions.RequestType requestType) {
        if (ibOrder.getIbAccount().isIbtoc2()) {
            C2Request cr = new C2Request();
            cr.setOrigin(IbloggerDefinitions.CONVERSION_ORIGIN_PREFIX_IB + ibOrder.getIbAccount().getAccountId());
            cr.setReferenceId(String.valueOf(ibOrder.getPermId()));
            cr.setRequestType(requestType);
            cr.setAction(IbApiEnums.Action.getEnumFromName(ibOrder.getAction()));
            cr.setQuantity(ibOrder.getQuantity());
            cr.setSymbol(ibOrder.getSymbol());
            cr.setSecType(IbApiEnums.SecType.getEnumFromName(ibOrder.getSecType()));
            cr.setOrderType(IbApiEnums.OrderType.getEnumFromName(ibOrder.getOrderType()));
            cr.setOrderPrice(ibOrder.getOrderPrice());
            cr.setTif(IbApiEnums.Tif.getEnumFromName(ibOrder.getTif()));
            try {
                mqSender.sendToC2pub(IbloggerUtil.toXml(cr));
            } catch (Exception e) {
                l.log(Level.SEVERE, "Error", e);
            }
        } else {
            l.info("IbToC2 processing is disabled, c2Request won't be sent");
        }
    }

    public void processExecution(IbOrder ibOrder) {
        if (ibOrder.getIbAccount().isAnalytics()) {
            IbExecution ie = new IbExecution();
            ie.setOrigin(IbloggerDefinitions.CONVERSION_ORIGIN_PREFIX_IB + ibOrder.getIbAccountId());
            ie.setReferenceId(String.valueOf(ibOrder.getPermId()));
            ie.setAction(IbApiEnums.Action.getEnumFromName(ibOrder.getAction()));
            ie.setQuantity(ibOrder.getQuantity());
            ie.setUnderlying(ibOrder.getUnderlying());
            ie.setCurrency(IbApiEnums.Currency.getEnumFromName(ibOrder.getCurrency()));
            ie.setSymbol(ibOrder.getSymbol());
            ie.setSecType(IbApiEnums.SecType.getEnumFromName(ibOrder.getSecType()));
            ie.setFillDate(ibOrder.getStatusDate());
            ie.setFillPrice(ibOrder.getFillPrice());
            try {
                mqSender.sendToAnalytics(IbloggerUtil.toXml(ie));
            } catch (Exception e) {
                l.log(Level.SEVERE, "Error", e);
            }
        } else {
            l.info("Analytics processing is disabled, ibExecution won't be sent");
        }
    }
}