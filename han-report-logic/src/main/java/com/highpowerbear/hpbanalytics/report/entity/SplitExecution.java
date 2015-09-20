package com.highpowerbear.hpbanalytics.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "rep_splitexecution")
public class SplitExecution implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="rep_splitexecution", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="rep_splitexecution")
    private Long id;
    private Integer splitQuantity;
    private Integer currentPosition;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateFilled;
    @ManyToOne
    @JsonIgnore
    Execution execution;
    @ManyToOne
    @JsonIgnore
    private Trade trade;

    @JsonProperty
    public Long getExecutionId() {
        return this.execution.getId();
    }

    @JsonProperty
    public Long getTradeId() {
        return this.trade.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SplitExecution that = (SplitExecution) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (currentPosition != null ? !currentPosition.equals(that.currentPosition) : that.currentPosition != null)
            return false;
        return !(execution != null ? !execution.equals(that.execution) : that.execution != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (currentPosition != null ? currentPosition.hashCode() : 0);
        result = 31 * result + (execution != null ? execution.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public Integer getSplitQuantity() {
        return splitQuantity;
    }

    public void setSplitQuantity(Integer splitQuantity) {
        this.splitQuantity = splitQuantity;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Calendar getDateFilled() {
        return dateFilled;
    }

    public void setDateFilled(Calendar dateFilled) {
        this.dateFilled = dateFilled;
    }

    public String print() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        return (id + ", " + execution.getAction() + ", " + execution.getSymbol() + ", " + splitQuantity + " (" + execution.getQuantity() + ")"+ ", "+ currentPosition + ", " + df.format(execution.getFillDate().getTime()));
    }
    
    @Override
    public String toString() {
        return "com.highpowerbear.analytics.db.entity.SplitExecution[ id=" + id + " ]";
    }
}
