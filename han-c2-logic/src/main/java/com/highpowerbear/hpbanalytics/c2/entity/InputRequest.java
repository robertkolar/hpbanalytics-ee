package com.highpowerbear.hpbanalytics.c2.entity;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by robertk on 3/28/15.
 */
@XmlRootElement(name = "c2Request")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="c2_inputrequest")
public class InputRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="c2_inputrequest", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="c2_inputrequest")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar receivedDate;
    @Enumerated(EnumType.STRING)
    private C2Definitions.InputStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar statusDate;
    @Enumerated(EnumType.STRING)
    private C2Definitions.IgnoreReason ignoreReason; // if ignored

    // jaxb parsed fields
    private String origin; // in case of IB origin --> IB:ibAccountId
    private String referenceId; // in case of IB origin --> permId
    @Enumerated(EnumType.STRING)
    private C2Definitions.RequestType requestType;
    @Enumerated(EnumType.STRING)
    private C2Definitions.Action action;
    private Integer quantity;
    private String symbol;
    @Enumerated(EnumType.STRING)
    private C2Definitions.SecType secType;
    @Enumerated(EnumType.STRING)
    private C2Definitions.OrderType orderType;
    private Double orderPrice;
    @Enumerated(EnumType.STRING)
    private C2Definitions.Tif tif;
    // end jaxb parsed fields

    public void changeStatus(C2Definitions.InputStatus newStatus) {
        this.status = newStatus;
        this.statusDate = Calendar.getInstance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Calendar createdDate) {
        this.receivedDate = createdDate;
    }

    public C2Definitions.InputStatus getStatus() {
        return status;
    }

    public void setStatus(C2Definitions.InputStatus inputStatus) {
        this.status = inputStatus;
    }

    public Calendar getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Calendar statusDate) {
        this.statusDate = statusDate;
    }

    public C2Definitions.IgnoreReason getIgnoreReason() {
        return ignoreReason;
    }

    public void setIgnoreReason(C2Definitions.IgnoreReason ignoreReason) {
        this.ignoreReason = ignoreReason;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String conversionOrigin) {
        this.origin = conversionOrigin;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public C2Definitions.RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(C2Definitions.RequestType requestType) {
        this.requestType = requestType;
    }

    public C2Definitions.Action getAction() {
        return action;
    }

    public void setAction(C2Definitions.Action action) {
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

    public C2Definitions.SecType getSecType() {
        return secType;
    }

    public void setSecType(C2Definitions.SecType secType) {
        this.secType = secType;
    }

    public C2Definitions.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(C2Definitions.OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public C2Definitions.Tif getTif() {
        return tif;
    }

    public void setTif(C2Definitions.Tif tif) {
        this.tif = tif;
    }

    public String print() {
        return "dbId=" + id + ", origin=" + origin + ", referenceId=" + referenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputRequest that = (InputRequest) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
