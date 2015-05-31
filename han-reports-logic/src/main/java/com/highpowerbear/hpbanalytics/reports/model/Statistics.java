package com.highpowerbear.hpbanalytics.reports.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Statistics  {
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
    
    public long getTimeInMillis() {
        return periodDate.getTimeInMillis();
    }
    
    public Double getCumulProfitLoss() {
        return cumulProfitLoss;
    }

    public void setCumulProfitLoss(Double cumulProfitLoss) {
        this.cumulProfitLoss = cumulProfitLoss;
    }
    
    public Double getLosersLoss() {
        return losersLoss;
    }

    public void setLosersLoss(Double losersLoss) {
        this.losersLoss = losersLoss;
    }

    public Double getMaxLoser() {
        return maxLoser;
    }

    public void setMaxLoser(Double maxLoser) {
        this.maxLoser = maxLoser;
    }

    public Double getMaxWinner() {
        return maxWinner;
    }

    public void setMaxWinner(Double maxWinner) {
        this.maxWinner = maxWinner;
    }

    public Calendar getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Calendar periodDate) {
        this.periodDate = periodDate;
    }

    public Integer getNumClosed() {
        return numClosed;
    }

    public void setNumClosed(Integer numClosed) {
        this.numClosed = numClosed;
    }

    public Integer getNumLosers() {
        return numLosers;
    }

    public void setNumLosers(Integer numLosers) {
        this.numLosers = numLosers;
    }

    public Integer getNumOpened() {
        return numOpened;
    }

    public void setNumOpened(Integer numOpened) {
        this.numOpened = numOpened;
    }

    public Integer getNumWinners() {
        return numWinners;
    }

    public void setNumWinners(Integer numWinners) {
        this.numWinners = numWinners;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Double getWinnersProfit() {
        return winnersProfit;
    }

    public void setWinnersProfit(Double winnersProfit) {
        this.winnersProfit = winnersProfit;
    }
}
