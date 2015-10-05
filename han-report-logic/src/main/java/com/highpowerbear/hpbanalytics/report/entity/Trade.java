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
import java.math.BigDecimal;
import java.math.MathContext;
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
    private BigDecimal avgOpenPrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar openDate;
    private BigDecimal avgClosePrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar closeDate;
    private BigDecimal profitLoss;
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
        return (closeDate != null ? ReportUtil.toDurationString(closeDate.getTimeInMillis() - openDate.getTimeInMillis()) : "");
    }

    public void calculate() {
        MathContext mc = new MathContext(5);
        this.report = splitExecutions.iterator().next().execution.getReport();
        this.type = (splitExecutions.iterator().next().getCurrentPosition() > 0 ? ReportDefinitions.TradeType.LONG : ReportDefinitions.TradeType.SHORT);
        this.symbol = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getSymbol());
        this.underlying = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getUnderlying());
        this.currency = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getCurrency());
        this.secType = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.iterator().next().execution.getSecType());
        this.openPosition = (this.splitExecutions == null || this.splitExecutions.isEmpty() ? null : this.splitExecutions.get(this.splitExecutions.size() - 1).getCurrentPosition());
        BigDecimal cumulativeOpenPrice = new BigDecimal(0.0);
        BigDecimal cumulativeClosePrice = new BigDecimal(0.0);
        this.cumulativeQuantity = 0;
        for (SplitExecution se : splitExecutions) {
            if ((this.type == ReportDefinitions.TradeType.LONG && se.execution.getAction() == ReportDefinitions.Action.BUY) || (this.type == ReportDefinitions.TradeType.SHORT && se.execution.getAction() == ReportDefinitions.Action.SELL)) {
                this.cumulativeQuantity += se.getSplitQuantity();
                cumulativeOpenPrice = cumulativeOpenPrice.add(new BigDecimal(se.getSplitQuantity()).multiply(se.execution.getFillPrice(), mc));
            }
            if (this.status == ReportDefinitions.TradeStatus.CLOSED) {
                if ((this.type == ReportDefinitions.TradeType.LONG && se.execution.getAction() == ReportDefinitions.Action.SELL) || (this.type == ReportDefinitions.TradeType.SHORT && se.execution.getAction() == ReportDefinitions.Action.BUY)) {
                    cumulativeClosePrice = cumulativeClosePrice.add(new BigDecimal(se.getSplitQuantity()).multiply(se.execution.getFillPrice(), mc));
                }
            }
        }
        this.avgOpenPrice = cumulativeOpenPrice.divide(new BigDecimal(this.cumulativeQuantity), mc);
        this.openDate = this.getSplitExecutions().get(0).getExecution().getFillDate();
        if (this.status == ReportDefinitions.TradeStatus.CLOSED) {
            this.avgClosePrice = cumulativeClosePrice.divide(new BigDecimal(this.cumulativeQuantity), mc);
            this.closeDate = this.getSplitExecutions().get(this.getSplitExecutions().size() - 1).getExecution().getFillDate();
            this.profitLoss = (ReportDefinitions.TradeType.LONG.equals(this.type) ? cumulativeClosePrice.subtract(cumulativeOpenPrice, mc) : cumulativeOpenPrice.subtract(cumulativeClosePrice, mc));
            if (ReportDefinitions.SecType.OPT.equals(getSecType())) {
                this.profitLoss = this.profitLoss.multiply((OptionParser.isMini(symbol) ? new BigDecimal(10) : new BigDecimal(100)), mc);
            }
            if (ReportDefinitions.SecType.FUT.equals(getSecType())) {
                this.profitLoss = this.profitLoss.multiply(new BigDecimal(ReportDefinitions.FuturePlMultiplier.getMultiplierByUnderlying(underlying)), mc);
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

    public Calendar getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Calendar openDate) {
        this.openDate = openDate;
    }

    public Calendar getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Calendar closeDate) {
        this.closeDate = closeDate;
    }

    public BigDecimal getAvgOpenPrice() {
        return avgOpenPrice;
    }

    public void setAvgOpenPrice(BigDecimal avgOpenPrice) {
        this.avgOpenPrice = avgOpenPrice;
    }

    public BigDecimal getAvgClosePrice() {
        return avgClosePrice;
    }

    public void setAvgClosePrice(BigDecimal avgClosePrice) {
        this.avgClosePrice = avgClosePrice;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
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
        return (id + ", " + type + ", " + status + ", " + symbol + ", " + secType + ", " + (openDate != null ? df.format(openDate.getTime()) : "-") + ", " + (closeDate != null ? df.format(closeDate.getTime()) : "-") + ", " + profitLoss);
    }

    @Override
    public String toString() {
        return "com.highpowerbear.analytics.db.entity.Trade[ id=" + id + " ]";
    }
}
