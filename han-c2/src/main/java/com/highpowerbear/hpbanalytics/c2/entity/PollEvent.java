package com.highpowerbear.hpbanalytics.c2.entity;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by robertk on 3/29/15.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "pollevent", schema = "c2", catalog = "hpbanalytics")
public class PollEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="pollevent", table="sequence", schema = "c2", catalog = "hpbanalytics", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="pollevent")
    private Long id;
    @XmlTransient
    @ManyToOne
    private C2Signal c2Signal;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @Enumerated(EnumType.STRING)
    private C2Definitions.PollStatus status;
    private String c2Request;
    private String c2Response;
    // parsed result
    private String datePosted;
    private String dateEmailed;
    private String dateKilled;
    private String dateExpired;
    private String dateTraded;
    private Double tradePrice;

    @XmlElement
    public Long getC2SignalDbId() {
        return c2Signal.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PollEvent that = (PollEvent) o;

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

    public C2Signal getC2Signal() {
        return c2Signal;
    }

    public void setC2Signal(C2Signal c2Signal) {
        this.c2Signal = c2Signal;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public C2Definitions.PollStatus getStatus() {
        return status;
    }

    public void setStatus(C2Definitions.PollStatus status) {
        this.status = status;
    }

    public String getC2Request() {
        return c2Request;
    }

    public void setC2Request(String c2Request) {
        this.c2Request = c2Request;
    }

    public String getC2Response() {
        return c2Response;
    }

    public void setC2Response(String c2Response) {
        this.c2Response = c2Response;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getDateEmailed() {
        return dateEmailed;
    }

    public void setDateEmailed(String dateEmailed) {
        this.dateEmailed = dateEmailed;
    }

    public String getDateKilled() {
        return dateKilled;
    }

    public void setDateKilled(String dateKilled) {
        this.dateKilled = dateKilled;
    }

    public String getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(String dateExpired) {
        this.dateExpired = dateExpired;
    }

    public String getDateTraded() {
        return dateTraded;
    }

    public void setDateTraded(String dateTraded) {
        this.dateTraded = dateTraded;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }
}
