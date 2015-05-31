package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.conversion.OutputProcessor;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbloggerDao;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/4/14.
 */
@Named
@ApplicationScoped
public class OpenOrderHandler {
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Inject private IbloggerDao ibloggerDao;
    @Inject private IbloggerData ibloggerData;
    @Inject private OutputProcessor outputProcessor;
    @Inject private HeartbeatControl heartbeatControl;

    public void handle(IbAccount ibAccount, int orderId, Contract contract, Order order, OrderState orderState) {
        if (!checkListenIb(ibAccount)) {
            l.info("IB listening disabled, order will be ignored");
            return;
        }
        if (!(ibAccount.mayProcessAccount(order.m_account))) {
            l.info("Account filter active, account=" + order.m_account + ", permitted=" + ibAccount.getPermittedAccounts() + ", order will be ignored");
            return;
        }
        if (!(ibAccount.mayProcessClient(order.m_clientId))) {
            l.info("Account filter active, clientId=" + order.m_clientId + ", permitted=" + ibAccount.getPermittedClients() + ", order will be ignored");
            return;
        }
        if (!checkOrderType(order.m_orderType)) {
            l.info("Unsupported order type=" + order.m_orderType + ", order will be ignored");
            return;
        }
        if (!checkSecType(contract.m_secType)) {
            l.info("Unsupported security type=" + contract.m_secType + ", order will be ignored");
            return;
        }
        if (!checkInstrumentFilter(ibAccount, contract.m_secType)) {
            l.info("Processing disabled, security type=" + contract.m_secType + ", order will be ignored");
            return;
        }

        String underlying = contract.m_symbol;
        String symbol = contract.m_localSymbol;

        if (symbol.split(" ").length > 1) {
            symbol = IbloggerUtil.removeSpace(symbol);
        }

        IbOrder ibOrderDb = ibloggerDao.getIbOrderByPermId(ibAccount, order.m_permId);
        if (ibOrderDb != null) {
            updateExistingOrder(ibOrderDb, order);
        } else {
            createNewOrder(ibAccount, orderId, contract, order, underlying, symbol);
        }
    }

    private boolean checkListenIb(IbAccount ibAccount) {
        return ibAccount.isListen();
    }

    private boolean checkOrderType(String orderType) {
        return  IbApiEnums.OrderType.LMT.name().equalsIgnoreCase(orderType) ||
                IbApiEnums.OrderType.STP.name().equalsIgnoreCase(orderType) ||
                IbApiEnums.OrderType.MKT.name().equalsIgnoreCase(orderType);
    }

    private boolean checkSecType(String secType) {
        return  IbApiEnums.SecType.STK.name().equalsIgnoreCase(secType) ||
                IbApiEnums.SecType.FUT.name().equalsIgnoreCase(secType) ||
                IbApiEnums.SecType.OPT.name().equalsIgnoreCase(secType) ||
                IbApiEnums.SecType.CASH.name().equalsIgnoreCase(secType);
    }

    private boolean checkInstrumentFilter(IbAccount ibAccount, String secType) {
        return  (IbApiEnums.SecType.STK.name().equalsIgnoreCase(secType) && ibAccount.isStk()) ||
                (IbApiEnums.SecType.FUT.name().equalsIgnoreCase(secType) && ibAccount.isFut()) ||
                (IbApiEnums.SecType.OPT.name().equalsIgnoreCase(secType) && ibAccount.isOpt()) ||
                (IbApiEnums.SecType.CASH.name().equalsIgnoreCase(secType) && ibAccount.isFx());
    }

    private void updateExistingOrder(IbOrder ibOrderDb, Order order) {
        if (!IbloggerDefinitions.IbOrderStatus.SUBMITTED.equals(ibOrderDb.getStatus()) && !IbloggerDefinitions.IbOrderStatus.UPDATED.equals(ibOrderDb.getStatus())) {
            return;
        }
        Double updatePrice = null;
        // if order already exists, check if the lmt/stp price has been updated
        if (IbApiEnums.OrderType.LMT.name().equalsIgnoreCase(order.m_orderType)) {
            if (ibOrderDb.getOrderPrice() != order.m_lmtPrice) {
                updatePrice = order.m_lmtPrice;
            }
        } else if (IbApiEnums.OrderType.STP.name().equalsIgnoreCase(order.m_orderType)) {
            if (ibOrderDb.getOrderPrice() != order.m_auxPrice) {
                updatePrice = order.m_auxPrice;
            }
        }
        if (updatePrice != null) {
            ibOrderDb.addEvent(IbloggerDefinitions.IbOrderStatus.UPDATED, updatePrice, null);
            ibloggerDao.updateIbOrder(ibOrderDb);
            outputProcessor.processConversion(ibOrderDb, IbloggerDefinitions.RequestType.UPDATE);
        }
    }

    private void createNewOrder(IbAccount ibAccount, int orderId, Contract contract, Order order, String underlying, String symbol) {
        IbOrder ibOrder = new IbOrder();
        ibOrder.setPermId(order.m_permId);
        ibOrder.setOrderId(orderId);
        ibOrder.setClientId(order.m_clientId);
        ibOrder.setIbAccount(ibAccount);
        ibOrder.setAction(order.m_action);
        ibOrder.setQuantity(order.m_totalQuantity);
        ibOrder.setUnderlying(underlying);
        ibOrder.setCurrency(contract.m_currency);
        ibOrder.setSymbol(symbol);
        ibOrder.setSecType(contract.m_secType);
        ibOrder.setOrderType(order.m_orderType);
        if (IbApiEnums.OrderType.LMT.name().equalsIgnoreCase(order.m_orderType)) {
            ibOrder.setOrderPrice(order.m_lmtPrice);
        } else if (IbApiEnums.OrderType.STP.name().equalsIgnoreCase(order.m_orderType)) {
            ibOrder.setOrderPrice(order.m_auxPrice);
        }
        ibOrder.setTif(order.m_tif);
        ibOrder.setParentId(order.m_parentId);
        ibOrder.setOcaGroup(order.m_ocaGroup);
        ibOrder.addEvent(IbloggerDefinitions.IbOrderStatus.SUBMITTED, null, null);
        ibloggerDao.newIbOrder(ibOrder);
        heartbeatControl.addHeartbeat(ibOrder);
        outputProcessor.processConversion(ibOrder, IbloggerDefinitions.RequestType.SUBMIT);
    }
}
