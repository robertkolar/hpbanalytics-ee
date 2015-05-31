package com.highpowerbear.hpbanalytics.iblogger.entity;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author Robert
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="ibl_iborderevent")
public class IbOrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="ibl_iborderevent", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="ibl_iborderevent")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @Enumerated(EnumType.STRING)
    private IbloggerDefinitions.IbOrderStatus status;
    private Double updatePrice;
    private Double fillPrice;
    @XmlTransient
    @ManyToOne
    private IbOrder ibOrder;

    @XmlElement
    public Long getIbOrderDbId() {
        return ibOrder.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public IbloggerDefinitions.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(IbloggerDefinitions.IbOrderStatus status) {
        this.status = status;
    }

    public Double getUpdatePrice() {
        return updatePrice;
    }

    public void setUpdatePrice(Double updatePrice) {
        this.updatePrice = updatePrice;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }

    public IbOrder getIbOrder() {
        return ibOrder;
    }

    public void setIbOrder(IbOrder ibOrder) {
        this.ibOrder = ibOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbOrderEvent that = (IbOrderEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
