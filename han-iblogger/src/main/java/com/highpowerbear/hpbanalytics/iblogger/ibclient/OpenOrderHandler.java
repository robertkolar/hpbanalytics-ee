package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.highpowerbear.hpbanalytics.iblogger.process.OutputProcessor;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/4/14.
 */
@ApplicationScoped
public class OpenOrderHandler {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private IbLoggerDao ibLoggerDao;
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
            symbol = IbLoggerUtil.removeSpace(symbol);
        }

        IbOrder ibOrderDb = ibLoggerDao.getIbOrderByPermId(ibAccount, order.m_permId);
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
                IbApiEnums.SecType.CASH.name().equalsIgnoreCase(secType) ||
                IbApiEnums.SecType.CFD.name().equalsIgnoreCase(secType);
    }

    private boolean checkInstrumentFilter(IbAccount ibAccount, String secType) {
        return  (IbApiEnums.SecType.STK.name().equalsIgnoreCase(secType) && ibAccount.isStk()) ||
                (IbApiEnums.SecType.FUT.name().equalsIgnoreCase(secType) && ibAccount.isFut()) ||
                (IbApiEnums.SecType.OPT.name().equalsIgnoreCase(secType) && ibAccount.isOpt()) ||
                (IbApiEnums.SecType.CASH.name().equalsIgnoreCase(secType) && ibAccount.isFx()) ||
                (IbApiEnums.SecType.CFD.name().equalsIgnoreCase(secType) && ibAccount.isCfd());
    }

    private void updateExistingOrder(IbOrder ibOrderDb, Order order) {
        if (ibOrderDb.getOrderId() == 0) {
            ibOrderDb.setOrderId(order.m_orderId);
            ibLoggerDao.updateIbOrder(ibOrderDb);
        }
        if (!IbLoggerDefinitions.IbOrderStatus.SUBMITTED.equals(ibOrderDb.getStatus()) && !IbLoggerDefinitions.IbOrderStatus.UPDATED.equals(ibOrderDb.getStatus())) {
            return;
        }
        boolean update = true;
        // if order already exists, check if the lmt/stp price has been updated
        if (IbApiEnums.OrderType.LMT.name().equalsIgnoreCase(order.m_orderType) && ibOrderDb.getOrderPrice() != order.m_lmtPrice) {
            ibOrderDb.setOrderPrice(order.m_lmtPrice);
        } else if (IbApiEnums.OrderType.STP.name().equalsIgnoreCase(order.m_orderType) && ibOrderDb.getOrderPrice() != order.m_auxPrice) {
            ibOrderDb.setOrderPrice(order.m_auxPrice);
        } else {
            update = false;
        }
        if (update) {
            ibOrderDb.addEvent(IbLoggerDefinitions.IbOrderStatus.UPDATED, ibOrderDb.getOrderPrice());
            ibLoggerDao.updateIbOrder(ibOrderDb);
            outputProcessor.processConversion(ibOrderDb, IbLoggerDefinitions.RequestType.UPDATE);
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
        ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.SUBMITTED, ibOrder.getOrderPrice());
        ibLoggerDao.newIbOrder(ibOrder);
        heartbeatControl.initHeartbeat(ibOrder);
        outputProcessor.processConversion(ibOrder, IbLoggerDefinitions.RequestType.SUBMIT);
    }
}
