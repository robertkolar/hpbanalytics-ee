package com.highpowerbear.hpbanalytics.report.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
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
    Execution execution;
    @ManyToOne
    private Trade trade;
    
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : super.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SplitExecution)) {
            return false;
        }
        SplitExecution other = (SplitExecution) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
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
