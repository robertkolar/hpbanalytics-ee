package com.highpowerbear.hpbanalytics.report.model;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;

import java.util.Date;

/**
 * Created by robertk on 9.10.2015.
 */
public class OptionParseResult {
    private String underlying;
    private ReportDefinitions.OptionType optType;
    private Date expDate;
    private Double strikePrice;

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public ReportDefinitions.OptionType getOptType() {
        return optType;
    }

    public void setOptType(ReportDefinitions.OptionType optType) {
        this.optType = optType;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }
}
