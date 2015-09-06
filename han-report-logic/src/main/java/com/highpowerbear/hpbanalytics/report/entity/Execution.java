package com.highpowerbear.hpbanalytics.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Robert
 */
@XmlRootElement(name = "ibExecution")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "rep_execution")
public class Execution implements Serializable, Comparable<Execution> {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="rep_execution", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="rep_execution")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar receivedDate;
    @ManyToOne
    @JsonIgnore
    private Report report;
    private String comment;
    private String origin; // in case of IB origin --> IB:ibAccountId
    private String referenceId; // in case of IB origin --> permId
    @Enumerated(EnumType.STRING)
    private RepDefinitions.Action action;
    private Integer quantity;
    private String symbol;
    private String underlying;
    @Enumerated(EnumType.STRING)
    private RepDefinitions.Currency currency;
    @Enumerated(EnumType.STRING)
    private RepDefinitions.SecType secType;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fillDate;
    private Double fillPrice;

    @JsonProperty
    public Integer getReportId() {
        return this.report.getId();
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

    public void setReceivedDate(Calendar receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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

    public RepDefinitions.Action getAction() {
        return action;
    }

    public void setAction(RepDefinitions.Action action) {
        this.action = action;
    }

    public RepDefinitions.SecType getSecType() {
        return secType;
    }

    public void setSecType(RepDefinitions.SecType secType) {
        this.secType = secType;
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

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public RepDefinitions.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(RepDefinitions.Currency currency) {
        this.currency = currency;
    }

    public Calendar getFillDate() {
        return fillDate;
    }
    
    public void setFillDate(Calendar dateFilled) {
        this.fillDate = dateFilled;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Execution execution = (Execution) o;

        return !(id != null ? !id.equals(execution.id) : execution.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Execution other) {
        return (this.fillDate.before(other.fillDate) ? -1 : (this.fillDate.after(other.fillDate) ? 1 : 0));
    }
    
    public String print() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        return (id + ", " + action + ", " + quantity + ", " + symbol + ", " + df.format(fillDate.getTime()));
    }
}
