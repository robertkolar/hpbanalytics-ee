package com.highpowerbear.hpbanalytics.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.common.ReportUtil;
import com.highpowerbear.hpbanalytics.report.process.OptionParser;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author robertk
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "rep_trade")
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="rep_trade", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="rep_trade")
    private Long id;
    @Enumerated(EnumType.STRING)
    private ReportDefinitions.TradeType type;
    private String symbol;
    private String underlying;
    @Enumerated(EnumType.STRING)
    private ReportDefinitions.Currency currency;
    @Enumerated(EnumType.STRING)
    private ReportDefinitions.SecType secType;
    private Integer cumulativeQuantity;
    @Enumerated(EnumType.STRING)
    private ReportDefinitions.TradeStatus status;
    private Integer openPosition;
    private Double avgOpenPrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateOpened;
    private Double avgClosePrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateClosed;
    private Double profitLoss;
    @ManyToOne
    @JsonIgnore
    private Report report;
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("dateFilled ASC")
    private List<SplitExecution> splitExecutions;

    @JsonProperty
    public Integer getReportId() {
        return this.report.getId();
    }

    @JsonProperty
    public String getDuration() {
        return (dateClosed != null ? ReportUtil.toDurationString(dateClosed.getTimeInMillis() - dateOpened.getTimeInMillis()) : "");
    }

    public void calculate() {
        this.report = splitExecutions.iterator().next().execution.getReport();
        this.type = (splitExecutions.iterator().next().getCurrentPosition() > 0 ? ReportDefinitions.TradeType.LONG : ReportDefinitions.TradeType.SHORT);
        this.symbol = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getSymbol());
        this.underlying = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getUnderlying());
        this.currency = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getCurrency());
        this.secType = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getSecType());
        this.openPosition = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.get(this.splitExecutions.size() - 1).getCurrentPosition());
        Double cumulativeOpenPrice = 0.0;
        Double cumulativeClosePrice = 0.0;
        this.cumulativeQuantity = 0;
        DecimalFormat df = (ReportDefinitions.SecType.CASH.equals(secType) ? new DecimalFormat("#.#####") : new DecimalFormat("#.##"));
        for (SplitExecution se : splitExecutions) {
            if ((this.type == ReportDefinitions.TradeType.LONG && se.execution.getAction() == ReportDefinitions.Action.BUY) || (this.type == ReportDefinitions.TradeType.SHORT && se.execution.getAction() == ReportDefinitions.Action.SELL)) {
                this.cumulativeQuantity += se.getSplitQuantity();
                cumulativeOpenPrice += se.getSplitQuantity() * se.execution.getFillPrice();
            }
            if (this.status == ReportDefinitions.TradeStatus.CLOSED) {
                if ((this.type == ReportDefinitions.TradeType.LONG && se.execution.getAction() == ReportDefinitions.Action.SELL) || (this.type == ReportDefinitions.TradeType.SHORT && se.execution.getAction() == ReportDefinitions.Action.BUY)) {
                    cumulativeClosePrice += se.getSplitQuantity() * se.execution.getFillPrice();
                }
            }
        }
        this.avgOpenPrice = Double.valueOf(df.format(cumulativeOpenPrice/this.cumulativeQuantity));
        this.dateOpened = this.getSplitExecutions().get(0).getExecution().getFillDate();
        if (this.status == ReportDefinitions.TradeStatus.CLOSED) {
            this.avgClosePrice = Double.valueOf(df.format(cumulativeClosePrice/this.cumulativeQuantity));
            this.dateClosed = this.getSplitExecutions().get(this.getSplitExecutions().size() - 1).getExecution().getFillDate();
            this.profitLoss = Double.valueOf(df.format(this.type == ReportDefinitions.TradeType.LONG ? cumulativeClosePrice - cumulativeOpenPrice : cumulativeOpenPrice - cumulativeClosePrice));
            if (ReportDefinitions.SecType.OPT.equals(getSecType())) {
                this.profitLoss *= (OptionParser.isMini(symbol) ? 10 : 100);
            }
            if (ReportDefinitions.SecType.FUT.equals(getSecType())) {
                this.profitLoss *= ReportDefinitions.FuturePlMultiplier.getMultiplierByUnderlying(underlying);
            }
        }
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

    public ReportDefinitions.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(ReportDefinitions.Currency currency) {
        this.currency = currency;
    }

    public ReportDefinitions.SecType getSecType() {
        return secType;
    }

    public void setSecType(ReportDefinitions.SecType secType) {
        this.secType = secType;
    }

    public Integer getOpenPosition() {
        return openPosition;
    }

    public void setOpenPosition(Integer openPosition) {
        this.openPosition = openPosition;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportDefinitions.TradeType getType() {
        return type;
    }

    public void setType(ReportDefinitions.TradeType type) {
        this.type = type;
    }

    public Integer getCumulativeQuantity() {
        return cumulativeQuantity;
    }

    public void setCumulativeQuantity(Integer cummulativeQuantity) {
        this.cumulativeQuantity = cummulativeQuantity;
    }

    public ReportDefinitions.TradeStatus getStatus() {
        return status;
    }

    public void setStatus(ReportDefinitions.TradeStatus status) {
        this.status = status;
    }

    public Double getAvgClosePrice() {
        return avgClosePrice;
    }

    public void setAvgClosePrice(Double avgClosePrice) {
        this.avgClosePrice = avgClosePrice;
    }

    public Calendar getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Calendar dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Double getAvgOpenPrice() {
        return avgOpenPrice;
    }

    public void setAvgOpenPrice(Double avgOpenPrice) {
        this.avgOpenPrice = avgOpenPrice;
    }

    public Calendar getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Calendar dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report source) {
        this.report = source;
    }

    public List<SplitExecution> getSplitExecutions() {
        return splitExecutions;
    }

    public void setSplitExecutions(List<SplitExecution> splitExecutions) {
        for (SplitExecution se : splitExecutions) {
            se.setTrade(this);
        }
        this.splitExecutions = splitExecutions;
    }

    public SplitExecution getLastSplitExecution() {
        return this.getSplitExecutions().get(this.getSplitExecutions().size() - 1);
    }
    
    public Boolean getOpen() {
        return (status == ReportDefinitions.TradeStatus.OPEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        return !(id != null ? !id.equals(trade.id) : trade.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String print() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        return (id + ", " + type + ", " + status + ", " + symbol + ", " + secType + ", " + (dateOpened != null ? df.format(dateOpened.getTime()) : "-") + ", " + (dateClosed != null ? df.format(dateClosed.getTime()) : "-") + ", " + profitLoss);
    }

    @Override
    public String toString() {
        return "com.highpowerbear.analytics.db.entity.Trade[ id=" + id + " ]";
    }
}
