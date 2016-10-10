package com.highpowerbear.hpbanalytics.report.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by robertk on 10/10/2016.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "exchangerate", schema = "report", catalog = "hpbanalytics")
public class ExchangeRate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String date; // yyyy-MM-dd
    private Double eurUsd;
    private Double gbpUsd;
    private Double audUsd;
    private Double nzdUsd;
    private Double usdChf;
    private Double usdJpy;
    private Double usdCad;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeRate that = (ExchangeRate) o;

        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getEurUsd() {
        return eurUsd;
    }

    public void setEurUsd(Double eurUsd) {
        this.eurUsd = eurUsd;
    }

    public Double getGbpUsd() {
        return gbpUsd;
    }

    public void setGbpUsd(Double gbpUsd) {
        this.gbpUsd = gbpUsd;
    }

    public Double getAudUsd() {
        return audUsd;
    }

    public void setAudUsd(Double audUsd) {
        this.audUsd = audUsd;
    }

    public Double getNzdUsd() {
        return nzdUsd;
    }

    public void setNzdUsd(Double nzdUsd) {
        this.nzdUsd = nzdUsd;
    }

    public Double getUsdChf() {
        return usdChf;
    }

    public void setUsdChf(Double usdChf) {
        this.usdChf = usdChf;
    }

    public Double getUsdJpy() {
        return usdJpy;
    }

    public void setUsdJpy(Double usdJpy) {
        this.usdJpy = usdJpy;
    }

    public Double getUsdCad() {
        return usdCad;
    }

    public void setUsdCad(Double usdCad) {
        this.usdCad = usdCad;
    }
}
