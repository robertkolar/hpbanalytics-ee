package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.SingletonRepo;
import com.highpowerbear.hpbanalytics.iblogger.conversion.OutputProcessor;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

/**
 *
 * @author Robert
 */

public class IbListenerImpl extends GenericIbListener {
    private IbLoggerDao ibLoggerDao = SingletonRepo.getInstance().getIbLoggerDao();
    private IbLoggerData ibLoggerData = SingletonRepo.getInstance().getIbLoggerData();
    private OpenOrderHandler openOrderHandler = SingletonRepo.getInstance().getOpenOrderHandler();
    private OutputProcessor outputProcessor = SingletonRepo.getInstance().getOutputProcessor();
    private HeartbeatControl heartbeatControl = SingletonRepo.getInstance().getHeartbeatControl();

    private IbAccount ibAccount;

    public IbListenerImpl(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        openOrderHandler.handle(ibAccount, orderId, contract, order, orderState);
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        if (!(  IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status))) {
            return;
        }
        IbOrder ibOrder = ibLoggerDao.getIbOrderByPermId(ibAccount, permId);
        if (ibOrder == null) {
            return;
        }
        if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && IbLoggerDefinitions.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.heartbeatReceived(ibOrder);
        } else if (IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status) && remaining == 0 && !IbLoggerDefinitions.IbOrderStatus.FILLED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.FILLED, null, avgFillPrice);
            ibLoggerDao.updateIbOrder(ibOrder);
            heartbeatControl.removeHeartbeat(ibOrder);
            outputProcessor.processExecution(ibOrder);
        } else if (IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) && !IbLoggerDefinitions.IbOrderStatus.CANCELLED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(IbLoggerDefinitions.IbOrderStatus.CANCELLED, null, null);
            ibLoggerDao.updateIbOrder(ibOrder);
            heartbeatControl.removeHeartbeat(ibOrder);
            outputProcessor.processConversion(ibOrder, IbLoggerDefinitions.RequestType.CANCEL);
        }
    }

    @Override
    public void managedAccounts(String accountsList) {
        super.managedAccounts(accountsList);
        ibLoggerData.getIbConnectionMap().get(ibAccount).setAccounts(accountsList);
    }
}
