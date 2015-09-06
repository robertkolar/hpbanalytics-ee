package com.highpowerbear.hpbanalytics.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Statistics  {
    private Integer id;
    private Calendar periodDate;
    private Integer numOpened;
    private Integer numClosed;
    private Integer numWinners;
    private Integer numLosers;
    private Double maxWinner;
    private Double maxLoser;
    private Double winnersProfit;
    private Double losersLoss;
    private Double profitLoss;
    private Double cumulProfitLoss;

    public Statistics() {
    }

    public Statistics(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Calendar getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Calendar periodDate) {
        this.periodDate = periodDate;
    }

    public Integer getNumOpened() {
        return numOpened;
    }

    public void setNumOpened(Integer numOpened) {
        this.numOpened = numOpened;
    }

    public Integer getNumClosed() {
        return numClosed;
    }

    public void setNumClosed(Integer numClosed) {
        this.numClosed = numClosed;
    }

    public Integer getNumWinners() {
        return numWinners;
    }

    public void setNumWinners(Integer numWinners) {
        this.numWinners = numWinners;
    }

    public Integer getNumLosers() {
        return numLosers;
    }

    public void setNumLosers(Integer numLosers) {
        this.numLosers = numLosers;
    }

    public Double getMaxWinner() {
        return maxWinner;
    }

    public void setMaxWinner(Double maxWinner) {
        this.maxWinner = maxWinner;
    }

    public Double getMaxLoser() {
        return maxLoser;
    }

    public void setMaxLoser(Double maxLoser) {
        this.maxLoser = maxLoser;
    }

    public Double getWinnersProfit() {
        return winnersProfit;
    }

    public void setWinnersProfit(Double winnersProfit) {
        this.winnersProfit = winnersProfit;
    }

    public Double getLosersLoss() {
        return losersLoss;
    }

    public void setLosersLoss(Double losersLoss) {
        this.losersLoss = losersLoss;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Double getCumulProfitLoss() {
        return cumulProfitLoss;
    }

    public void setCumulProfitLoss(Double cumulProfitLoss) {
        this.cumulProfitLoss = cumulProfitLoss;
    }
}
