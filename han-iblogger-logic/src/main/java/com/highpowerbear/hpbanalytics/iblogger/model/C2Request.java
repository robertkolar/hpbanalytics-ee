package com.highpowerbear.hpbanalytics.iblogger.model;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbApiEnums;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by robertk on 3/29/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class C2Request {
    private String origin; // IB:ibAccountId
    private String referenceId; // permId
    private IbLoggerDefinitions.RequestType requestType;
    private IbApiEnums.Action action;
    private Integer quantity;
    private String symbol;
    private IbApiEnums.SecType secType;
    private IbApiEnums.OrderType orderType;
    private Double orderPrice;
    private IbApiEnums.Tif tif;
    private String ocaGroup;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public IbLoggerDefinitions.RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(IbLoggerDefinitions.RequestType requestType) {
        this.requestType = requestType;
    }

    public IbApiEnums.Action getAction() {
        return action;
    }

    public void setAction(IbApiEnums.Action action) {
        this.action = action;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public IbApiEnums.SecType getSecType() {
        return secType;
    }

    public void setSecType(IbApiEnums.SecType secType) {
        this.secType = secType;
    }

    public IbApiEnums.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(IbApiEnums.OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public IbApiEnums.Tif getTif() {
        return tif;
    }

    public void setTif(IbApiEnums.Tif tif) {
        this.tif = tif;
    }

    public String getOcaGroup() {
        return ocaGroup;
    }

    public void setOcaGroup(String ocaGroup) {
        this.ocaGroup = ocaGroup;
    }
}
