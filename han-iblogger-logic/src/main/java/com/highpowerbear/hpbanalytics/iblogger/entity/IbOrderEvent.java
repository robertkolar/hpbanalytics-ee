package com.highpowerbear.hpbanalytics.iblogger.entity;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;

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
@Table(name = "iborderevent", schema = "iblogger", catalog = "hpbanalytics")
public class IbOrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="iborderevent", table="sequence", schema = "iblogger", catalog = "hpbanalytics", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="iborderevent")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @Enumerated(EnumType.STRING)
    private IbLoggerDefinitions.IbOrderStatus status;
    private Double price;
    @XmlTransient
    @ManyToOne
    private IbOrder ibOrder;

    @XmlElement
    public Long getIbOrderDbId() {
        return ibOrder.getId();
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

    public IbLoggerDefinitions.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(IbLoggerDefinitions.IbOrderStatus status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public IbOrder getIbOrder() {
        return ibOrder;
    }

    public void setIbOrder(IbOrder ibOrder) {
        this.ibOrder = ibOrder;
    }
}
