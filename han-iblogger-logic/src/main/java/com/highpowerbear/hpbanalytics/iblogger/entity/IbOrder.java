package com.highpowerbear.hpbanalytics.iblogger.entity;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Robert
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "iborder", schema = "iblogger", catalog = "hpbanalytics")
public class IbOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="iborder", table="sequence", schema = "iblogger", catalog = "hpbanalytics", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="iborder")
    private Long id;
    private Integer permId;
    private Integer orderId;
    private Integer clientId;
    @XmlTransient
    @ManyToOne
    private IbAccount ibAccount;
    private String action;
    private Integer quantity;
    private String underlying;
    private String currency;
    private String symbol;
    private String secType;
    private String orderType;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar submitDate;
    private Double orderPrice;
    private String tif;
    private Integer parentId;
    private String ocaGroup;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar statusDate;
    private Double fillPrice;
    @Enumerated(EnumType.STRING)
    private IbLoggerDefinitions.IbOrderStatus status;
    @OneToMany(mappedBy = "ibOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate DESC, id DESC")
    private List<IbOrderEvent> ibOrderEvents;
    @Transient
    private Integer heartbeatCount;

    @XmlElement
    public String getIbAccountId() {
        return ibAccount.getAccountId();
    }

    public void addEvent(IbLoggerDefinitions.IbOrderStatus status, Double updatePrice, Double fillPrice) {
        this.status = status;
        this.statusDate = Calendar.getInstance();
        IbOrderEvent e = new IbOrderEvent();
        e.setIbOrder(this);
        e.setEventDate(this.statusDate );
        e.setStatus(this.status);
        e.setUpdatePrice(updatePrice);
        e.setFillPrice(fillPrice);
        if (IbLoggerDefinitions.IbOrderStatus.SUBMITTED.equals(status)) {
            this.submitDate = this.statusDate;
        }
        if (IbLoggerDefinitions.IbOrderStatus.UPDATED.equals(status)) {
            this.orderPrice = updatePrice;
        }
        if (IbLoggerDefinitions.IbOrderStatus.FILLED.equals(status)) {
            this.fillPrice = e.getFillPrice();
        }
        if (ibOrderEvents == null) {
            ibOrderEvents = new ArrayList<>();
        }
        ibOrderEvents.add(e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbOrder ibOrder = (IbOrder) o;

        return !(id != null ? !id.equals(ibOrder.id) : ibOrder.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPermId() {
        return permId;
    }

    public void setPermId(Integer permId) {
        this.permId = permId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public IbAccount getIbAccount() {
        return ibAccount;
    }

    public void setIbAccount(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Calendar getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Calendar submitDate) {
        this.submitDate = submitDate;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getTif() {
        return tif;
    }

    public void setTif(String tif) {
        this.tif = tif;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getOcaGroup() {
        return ocaGroup;
    }

    public void setOcaGroup(String ocaGroup) {
        this.ocaGroup = ocaGroup;
    }

    public Calendar getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Calendar fillDate) {
        this.statusDate = fillDate;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }

    public IbLoggerDefinitions.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(IbLoggerDefinitions.IbOrderStatus status) {
        this.status = status;
    }

    public List<IbOrderEvent> getIbOrderEvents() {
        return ibOrderEvents;
    }

    public void setIbOrderEvents(List<IbOrderEvent> events) {
        this.ibOrderEvents = events;
    }

    public Integer getHeartbeatCount() {
        return heartbeatCount;
    }

    public void setHeartbeatCount(Integer heartbeatCount) {
        this.heartbeatCount = heartbeatCount;
    }
}
