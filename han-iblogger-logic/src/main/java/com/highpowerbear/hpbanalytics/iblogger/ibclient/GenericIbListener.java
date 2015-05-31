package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.ib.client.*;

import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
public class GenericIbListener implements EWrapper {
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        l.info(EWrapperMsgGenerator.bondContractDetails(reqId, contractDetails));
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        l.info(EWrapperMsgGenerator.contractDetails(reqId, contractDetails));
    }

    @Override
    public void	contractDetailsEnd(int reqId) {
        l.info(EWrapperMsgGenerator.contractDetailsEnd(reqId));
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        l.info(EWrapperMsgGenerator.fundamentalData(reqId, data));
    }

    @Override
    public void currentTime(long time) {
        l.info(EWrapperMsgGenerator.currentTime(time));
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        l.info(EWrapperMsgGenerator.execDetails(reqId, contract, execution));
    }

    @Override
    public void execDetailsEnd( int reqId){
        l.info(EWrapperMsgGenerator.execDetailsEnd(reqId));
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        l.info(EWrapperMsgGenerator.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps));
    }

    @Override
    public void managedAccounts(String accountsList) {
        l.info(EWrapperMsgGenerator.managedAccounts(accountsList));
    }

    @Override
    public void nextValidId(int orderId) {
        l.info(EWrapperMsgGenerator.nextValidId(orderId));
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        l.info(EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState));
    }

    @Override
    public void openOrderEnd() {
        l.info(EWrapperMsgGenerator.openOrderEnd());
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        l.info(EWrapperMsgGenerator.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld));
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
        l.info(EWrapperMsgGenerator.realtimeBar(reqId, time, open, high, low, close, volume, wap, count));
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        l.info(EWrapperMsgGenerator.receiveFA(faDataType, xml));
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        l.info(EWrapperMsgGenerator.scannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr));
    }

    @Override
    public void scannerDataEnd(int reqId) {
        l.info(EWrapperMsgGenerator.scannerDataEnd(reqId));
    }

    @Override
    public void scannerParameters(String xml) {
        l.info(EWrapperMsgGenerator.scannerParameters(xml));
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {
        l.info(EWrapperMsgGenerator.tickEFP(tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry));
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        l.info(EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value));
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        l.info(EWrapperMsgGenerator.tickOptionComputation(tickerId, field, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice));
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        l.info(EWrapperMsgGenerator.tickPrice(tickerId, field, price, canAutoExecute));
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        l.info(EWrapperMsgGenerator.tickSize(tickerId, field, size));
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        l.info(EWrapperMsgGenerator.tickString(tickerId, tickType, value));
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        l.info(EWrapperMsgGenerator.updateAccountTime(timeStamp));
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
        l.info(EWrapperMsgGenerator.updateAccountValue(key, value, currency, accountName));
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        l.info(EWrapperMsgGenerator.accountDownloadEnd(accountName));
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
        l.info(EWrapperMsgGenerator.updateMktDepth(tickerId, position, operation, side, price, size));
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
        l.info(EWrapperMsgGenerator.updateMktDepthL2(tickerId, position, marketMaker, operation, side, price, size));
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        l.info(EWrapperMsgGenerator.updateNewsBulletin(msgId, msgType, message, origExchange));
    }

    @Override
    public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        l.info(EWrapperMsgGenerator.updatePortfolio(contract, position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName));
    }

    @Override
    public void connectionClosed() {
        l.info(EWrapperMsgGenerator.connectionClosed());
    }

    @Override
    public void error(Exception e) {
        l.info(EWrapperMsgGenerator.error(e));
    }

    @Override
    public void error(String str) {
        l.info(EWrapperMsgGenerator.error(str));
    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        l.info(EWrapperMsgGenerator.error(id, errorCode, errorMsg));
    }

    @Override
    public void deltaNeutralValidation(int reqId, UnderComp underComp) {
        l.info(EWrapperMsgGenerator.deltaNeutralValidation(reqId, underComp));
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        l.info(EWrapperMsgGenerator.tickSnapshotEnd(reqId));
    }
    
    @Override
    public void marketDataType(int reqId, int marketDataType) {
        l.info(EWrapperMsgGenerator.marketDataType(reqId, marketDataType));
    }
    
    @Override
    public void commissionReport(CommissionReport commissionReport) {
        l.info(EWrapperMsgGenerator.commissionReport(commissionReport));
    }

    @Override
    public void position(String account, Contract contract, int pos, double avgCost) {
        l.info(EWrapperMsgGenerator.position(account, contract, pos, avgCost));
    }

    @Override
    public void positionEnd() {
        l.info(EWrapperMsgGenerator.positionEnd());
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        l.info(EWrapperMsgGenerator.accountSummary(reqId, account, tag, value, currency));
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        l.info(EWrapperMsgGenerator.accountSummaryEnd(reqId));
    }

    @Override
    public void verifyMessageAPI(String apiData) {
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
    }
}
