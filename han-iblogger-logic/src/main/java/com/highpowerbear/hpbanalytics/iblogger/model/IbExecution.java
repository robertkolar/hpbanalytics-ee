package com.highpowerbear.hpbanalytics.iblogger.model;

import com.highpowerbear.hpbanalytics.iblogger.ibclient.IbApiEnums;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 * Created by robertk on 4/6/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IbExecution {
    private String origin; // IB:ibAccountId
    private String referenceId; // permId
    private IbApiEnums.Action action;
    private Integer quantity;
    private String underlying;
    private IbApiEnums.Currency currency;
    private String symbol;
    private IbApiEnums.SecType secType;
    private Calendar fillDate;
    private Double fillPrice;

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

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public IbApiEnums.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(IbApiEnums.Currency currency) {
        this.currency = currency;
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

    public Calendar getFillDate() {
        return fillDate;
    }

    public void setFillDate(Calendar fillDate) {
        this.fillDate = fillDate;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }
}
