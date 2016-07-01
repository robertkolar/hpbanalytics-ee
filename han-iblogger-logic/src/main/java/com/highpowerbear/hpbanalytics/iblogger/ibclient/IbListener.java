package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.highpowerbear.hpbanalytics.iblogger.process.OutputProcessor;
import com.highpowerbear.hpbanalytics.iblogger.websocket.WebsocketController;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author Robert
 */
@Dependent
public class IbListener extends GenericIbListener {

    @Inject private IbLoggerDao ibLoggerDao;
    @Inject private OpenOrderHandler openOrderHandler;
    @Inject private OutputProcessor outputProcessor;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private WebsocketController websocketController;

    private IbAccount ibAccount;

    public IbListener configure(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
        return this;
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        openOrderHandler.handle(ibAccount, orderId, contract, order, orderState);
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        if (!(  IbApiEnums.OrderStatus.SUBMITTED.getValue().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.PRESUBMITTED.getValue().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.CANCELLED.getValue().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.FILLED.getValue().equalsIgnoreCase(status))) {
            return;
        }
        IbOrder ibOrder = ibLoggerDao.getIbOrderByPermId(ibAccount, permId);
        if (ibOrder == null) {
            return;
        }
        if ((IbApiEnums.OrderStatus.SUBMITTED.getValue().equalsIgnoreCase(status) || IbApiEnums.OrderStatus.PRESUBMITTED.getValue().equalsIgnoreCase(status)) && IbLoggerDefinitions.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.initHeartbeat(ibOrder);
        } else if (IbApiEnums.OrderStatus.FILLED.getValue().equalsIgnoreCase(status) && remaining == 0 && !IbLoggerDefinitions.IbOrderStatus.FILLED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.FILLED, avgFillPrice);
            ibLoggerDao.updateIbOrder(ibOrder);
            heartbeatControl.removeHeartbeat(ibOrder);
            outputProcessor.processExecution(ibOrder);
        } else if (IbApiEnums.OrderStatus.CANCELLED.getValue().equalsIgnoreCase(status) && !IbLoggerDefinitions.IbOrderStatus.CANCELLED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.CANCELLED, null);
            ibLoggerDao.updateIbOrder(ibOrder);
            heartbeatControl.removeHeartbeat(ibOrder);
            outputProcessor.processConversion(ibOrder, IbLoggerDefinitions.RequestType.CANCEL);
        }
        websocketController.broadcastIbLoggerMessage("order status changed");
    }

    @Override
    public void managedAccounts(String accountsList) {
        super.managedAccounts(accountsList);
        ibController.getIbConnectionMap().get(ibAccount).setAccounts(accountsList);
    }
}
