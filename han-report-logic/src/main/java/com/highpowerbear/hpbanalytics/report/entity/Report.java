package com.highpowerbear.hpbanalytics.report.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author Robert
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "rep_report")
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private Integer id;
    private String origin; // in case of IB origin --> IB:ibAccountId
    private String name;
    @Transient
    private Long numExecutions;
    @Transient
    private Long numTrades;
    @Transient
    private Long numOpenTrades;
    @Transient
    private Long numUnderlyings;
    @Transient
    private Long numOpenUnderlyings;
    @Transient
    private Calendar firstExecutionDate;
    @Transient
    private Calendar lastExecutionDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumExecutions() {
        return numExecutions;
    }

    public void setNumExecutions(Long numExecutions) {
        this.numExecutions = numExecutions;
    }

    public Long getNumTrades() {
        return numTrades;
    }

    public void setNumTrades(Long numTrades) {
        this.numTrades = numTrades;
    }

    public Long getNumOpenTrades() {
        return numOpenTrades;
    }

    public void setNumOpenTrades(Long numOpenTrades) {
        this.numOpenTrades = numOpenTrades;
    }

    public Long getNumUnderlyings() {
        return numUnderlyings;
    }

    public void setNumUnderlyings(Long numUnderlyings) {
        this.numUnderlyings = numUnderlyings;
    }

    public Long getNumOpenUnderlyings() {
        return numOpenUnderlyings;
    }

    public void setNumOpenUnderlyings(Long numOpenUnderlyings) {
        this.numOpenUnderlyings = numOpenUnderlyings;
    }

    public Calendar getFirstExecutionDate() {
        return firstExecutionDate;
    }

    public void setFirstExecutionDate(Calendar firstExecutionDate) {
        this.firstExecutionDate = firstExecutionDate;
    }

    public Calendar getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Calendar lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        return !(id != null ? !id.equals(report.id) : report.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
