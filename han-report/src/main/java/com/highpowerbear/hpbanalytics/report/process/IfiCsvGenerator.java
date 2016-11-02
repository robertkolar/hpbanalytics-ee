package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.common.ReportUtil;
import com.highpowerbear.hpbanalytics.report.entity.ExchangeRate;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.SplitExecution;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by robertk on 10/10/2016.
 */
@ApplicationScoped
public class IfiCsvGenerator {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private ReportDao reportDao;

    private final Integer optionMultiplier = 100;
    private final String NL = "\n";
    private final String DL = ";";
    private final String acquireType = "A - nakup";
    private Map<ReportDefinitions.SecType, String> secTypeMap = new HashMap<>();
    private Map<ReportDefinitions.TradeType, String> tradeTypeMap = new HashMap<>();
    private DateFormat df = new SimpleDateFormat("dd. MM. yyyy");
    private DateFormat dfLog = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat dfRate = new SimpleDateFormat("yyyy-MM-dd");
    private NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

    @PostConstruct
    private void init() {
        secTypeMap.put(ReportDefinitions.SecType.FUT, "01 - terminska pogodba");
        secTypeMap.put(ReportDefinitions.SecType.CFD, "02 - pogodba na razliko");
        secTypeMap.put(ReportDefinitions.SecType.OPT, "03 - opcija");
        tradeTypeMap.put(ReportDefinitions.TradeType.LONG, "običajni");
        tradeTypeMap.put(ReportDefinitions.TradeType.SHORT, "na kratko");
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
    }

    public String generate(Report report, Integer year, ReportDefinitions.TradeType tradeType) {
        l.info("BEGIN IfiCsvGenerator.generatem, report=" + report.getId() + ", year=" + year + ", tradeType=" + tradeType);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        Calendar beginDate = ReportUtil.toBeginOfPeriod(cal, ReportDefinitions.StatisticsInterval.YEAR);
        cal.set(Calendar.YEAR, year + 1);
        Calendar endDate = ReportUtil.toBeginOfPeriod(cal, ReportDefinitions.StatisticsInterval.YEAR);
        List<Trade> trades = reportDao.getTradesBetweenDates(report, beginDate, endDate, tradeType);
        l.info("Begin date=" + dfLog.format(beginDate.getTime()) + ", endDate=" + dfLog.format(endDate.getTime()) + ", trades=" + trades.size());
        StringBuilder sb = new StringBuilder();
        if (ReportDefinitions.TradeType.SHORT.equals(tradeType)) {
            writeCsvHeaderShort(sb);
        } else if (ReportDefinitions.TradeType.LONG.equals(tradeType)) {
            writeCsvHeaderLong(sb);
        }
        int i = 0;
        for (Trade trade : trades) {
            if (!(
                    ReportDefinitions.SecType.FUT.equals(trade.getSecType()) ||
                    ReportDefinitions.SecType.OPT.equals(trade.getSecType()) ||
                    ReportDefinitions.SecType.CFD.equals(trade.getSecType()))
            ) {
                continue;
            }
            i++;
            writeTrade(sb, trade, i);
            List<SplitExecution> splitExecutions = trade.getSplitExecutions();
            int j = 0;
            for (SplitExecution se : splitExecutions) {
                j++;
                if (ReportDefinitions.TradeType.SHORT.equals(tradeType) && ReportDefinitions.Action.SELL.equals(se.getExecution().getAction())) {
                    writeTradeShortSplitExecutionSell(sb, se, i, j);
                } else if (ReportDefinitions.TradeType.SHORT.equals(tradeType) && ReportDefinitions.Action.BUY.equals(se.getExecution().getAction())) {
                    writeTradeShortSplitExecutionBuy(sb, se, i, j);
                } else if (ReportDefinitions.TradeType.LONG.equals(tradeType) && ReportDefinitions.Action.BUY.equals(se.getExecution().getAction())) {
                    writeTradeLongSplitExecutionBuy(sb, se, i, j);
                } else if (ReportDefinitions.TradeType.LONG.equals(tradeType) && ReportDefinitions.Action.SELL.equals(se.getExecution().getAction())) {
                    writeTradeLongSplitExecutionSell(sb, se, i, j);
                }
            }
            sb.append(NL);
        }
        l.info("END IfiCsvGenerator.generatem, report=" + report.getId() + ", year=" + year + ", tradeType=" + tradeType);
        return sb.toString();
    }

    private void writeCsvHeaderShort(StringBuilder sb) {
        sb.append("Zap. št.").append(DL);
        sb.append("Vrsta IFI").append(DL);
        sb.append("Vrsta posla").append(DL);
        sb.append("Trgovalna koda").append(DL);
        sb.append("Datum odsvojitve").append(DL);
        sb.append("Količina odsvojenega IFI").append(DL);
        sb.append("Vrednost ob odsvojitvi (na enoto) USD").append(DL);
        sb.append("Vrednost ob odsvojitvi (na enoto) EUR").append(DL);
        sb.append("Datum pridobitve").append(DL);
        sb.append("Način pridobitve").append(DL);
        sb.append("Količina").append(DL);
        sb.append("Vrednost ob pridobitvi na enoto) USD").append(DL);
        sb.append("Vrednost ob pridobitvi (na enoto) EUR").append(DL);
        sb.append("Zaloga IFI").append(DL);
        sb.append("Dobiček Izguba EUR").append(NL);
    }

    private void writeCsvHeaderLong(StringBuilder sb) {
        sb.append("Zap. št.").append(DL);
        sb.append("Vrsta IFI").append(DL);
        sb.append("Vrsta posla").append(DL);
        sb.append("Trgovalna koda").append(DL);
        sb.append("Datum pridobitve").append(DL);
        sb.append("Način pridobitve").append(DL);
        sb.append("Količina").append(DL);
        sb.append("Nabavna vrednost ob pridobitvi (na enoto) USD").append(DL);
        sb.append("Nabavna vrednost ob pridobitvi (na enoto) EUR").append(DL);
        sb.append("Datum odsvojitve").append(DL);
        sb.append("Količina odsvojenega IFI").append(DL);
        sb.append("Vrednost ob odsvojitvi (na enoto) USD").append(DL);
        sb.append("Vrednost ob odsvojitvi (na enoto) EUR").append(DL);
        sb.append("Zaloga IFI").append(DL);
        sb.append("Dobiček Izguba EUR").append(NL);
    }

    private void writeTrade(StringBuilder sb, Trade trade, int i) {
        sb.append(i).append(DL);
        sb.append(secTypeMap.get(trade.getSecType())).append(DL);
        sb.append(tradeTypeMap.get(trade.getType())).append(DL);
        sb.append(trade.getSymbol()).append(DL);
        for (int k = 0; k < 10; k++) {
            sb.append(DL);
        }
        Double profitLoss = calculatePlEur(trade);
        sb.append(profitLoss != null ? nf.format(profitLoss) : "");
        sb.append(NL);
    }

    private Double calculatePlEur(Trade trade) {
        if (!ReportDefinitions.TradeStatus.CLOSED.equals(trade.getStatus())) {
            return null;
        }
        Double cumulativeOpenPrice = 0d;
        Double cumulativeClosePrice = 0d;
        boolean firstStepOk = true;
        // first step
        for (SplitExecution se : trade.getSplitExecutions()) {
            ExchangeRate exchangeRate = reportDao.getExchangeRate(dfRate.format(se.getFillDate().getTime()));
            if (exchangeRate == null) {
                firstStepOk = false;
                break;
            }
            Double fillPrice = se.getExecution().getFillPrice().doubleValue() / exchangeRate.getEurUsd();

            if (    (ReportDefinitions.TradeType.LONG. equals(trade.getType()) && ReportDefinitions.Action.BUY. equals(se.getExecution().getAction())) ||
                    (ReportDefinitions.TradeType.SHORT.equals(trade.getType()) && ReportDefinitions.Action.SELL.equals(se.getExecution().getAction()))) {

                cumulativeOpenPrice += se.getSplitQuantity() * fillPrice;
            }
            if (ReportDefinitions.TradeStatus.CLOSED.equals(trade.getStatus())) {
                if (    (ReportDefinitions.TradeType.LONG. equals(trade.getType()) && ReportDefinitions.Action.SELL.equals(se.getExecution().getAction())) ||
                        (ReportDefinitions.TradeType.SHORT.equals(trade.getType()) && ReportDefinitions.Action.BUY. equals(se.getExecution().getAction()))) {

                    cumulativeClosePrice += se.getSplitQuantity() * fillPrice;
                }
            }
        }
        if (!firstStepOk) {
            return null;
        }
        // second step
        Double profitLoss = (ReportDefinitions.TradeType.LONG.equals(trade.getType()) ? cumulativeClosePrice - cumulativeOpenPrice : cumulativeOpenPrice - cumulativeClosePrice);
        if (ReportDefinitions.SecType.OPT.equals(trade.getSecType())) {
            profitLoss *= OptionUtil.isMini(trade.getSymbol()) ? 10d : 100d;
        }
        if (ReportDefinitions.SecType.FUT.equals(trade.getSecType())) {
            profitLoss *= ReportDefinitions.FuturePlMultiplier.getMultiplierByUnderlying(trade.getUnderlying());
        }
        return profitLoss;
    }

    private void writeTradeShortSplitExecutionSell(StringBuilder sb, SplitExecution se, int i, int j) {
        ExchangeRate exchangeRate = reportDao.getExchangeRate(dfRate.format(se.getFillDate().getTime()));
        if (exchangeRate == null) {
            return;
        }
        sb.append(i).append("-").append(j).append(DL).append(DL).append(DL).append(DL);
        sb.append(df.format(se.getFillDate().getTime())).append(DL);
        sb.append(se.getSplitQuantity()).append(DL);
        Double fillPrice = se.getExecution().getFillPrice().doubleValue();
        if (ReportDefinitions.SecType.OPT.equals(se.getExecution().getSecType())) {
            fillPrice = fillPrice * optionMultiplier;
        }
        sb.append(nf.format(fillPrice)).append(DL);
        sb.append(nf.format(fillPrice / exchangeRate.getEurUsd())).append(DL);
        for (int k = 0; k < 7; k++) {
            sb.append(DL);
        }
        sb.append(NL);
    }

    private void writeTradeShortSplitExecutionBuy(StringBuilder sb, SplitExecution se, int i, int j) {
        ExchangeRate exchangeRate = reportDao.getExchangeRate(dfRate.format(se.getFillDate().getTime()));
        if (exchangeRate == null) {
            return;
        }
        sb.append(i).append("-").append(j);
        for (int k = 0; k < 8; k++) {
            sb.append(DL);
        }
        sb.append(df.format(se.getFillDate().getTime())).append(DL);
        sb.append(acquireType).append(DL);
        sb.append(se.getSplitQuantity()).append(DL);
        Double fillPrice = se.getExecution().getFillPrice().doubleValue();
        if (ReportDefinitions.SecType.OPT.equals(se.getExecution().getSecType())) {
            fillPrice = fillPrice * optionMultiplier;
        }
        sb.append(nf.format(fillPrice)).append(DL);
        sb.append(nf.format(fillPrice / exchangeRate.getEurUsd())).append(DL);
        sb.append(se.getCurrentPosition()).append(DL);
        sb.append(NL);
    }

    private void writeTradeLongSplitExecutionBuy(StringBuilder sb, SplitExecution se, int i, int j) {
        ExchangeRate exchangeRate = reportDao.getExchangeRate(dfRate.format(se.getFillDate().getTime()));
        if (exchangeRate == null) {
            return;
        }
        sb.append(i).append("-").append(j).append(DL).append(DL).append(DL).append(DL);
        sb.append(df.format(se.getFillDate().getTime())).append(DL);
        sb.append(acquireType).append(DL);
        sb.append(se.getSplitQuantity()).append(DL);
        Double fillPrice = se.getExecution().getFillPrice().doubleValue();
        if (ReportDefinitions.SecType.OPT.equals(se.getExecution().getSecType())) {
            fillPrice = fillPrice * optionMultiplier;
        }
        sb.append(nf.format(fillPrice)).append(DL);
        sb.append(nf.format(fillPrice / exchangeRate.getEurUsd())).append(DL);
        sb.append(NL);
    }

    private void writeTradeLongSplitExecutionSell(StringBuilder sb, SplitExecution se, int i, int j) {
        ExchangeRate exchangeRate = reportDao.getExchangeRate(dfRate.format(se.getFillDate().getTime()));
        if (exchangeRate == null) {
            return;
        }
        sb.append(i).append("-").append(j);
        for (int k = 0; k < 8; k++) {
            sb.append(DL);
        }
        sb.append(df.format(se.getFillDate().getTime())).append(DL);
        sb.append(se.getSplitQuantity()).append(DL);
        Double fillPrice = se.getExecution().getFillPrice().doubleValue();
        if (ReportDefinitions.SecType.OPT.equals(se.getExecution().getSecType())) {
            fillPrice = fillPrice * optionMultiplier;
        }
        sb.append(nf.format(fillPrice)).append(DL);
        sb.append(nf.format(fillPrice / exchangeRate.getEurUsd())).append(DL);
        for (int k = 0; k < 7; k++) {
            sb.append(DL);
        }
        sb.append(se.getCurrentPosition()).append(DL);
        sb.append(NL);
    }
}
