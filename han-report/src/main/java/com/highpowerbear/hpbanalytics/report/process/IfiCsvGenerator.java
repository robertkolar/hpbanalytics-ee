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

/**
 * Created by robertk on 10/10/2016.
 */
@ApplicationScoped
public class IfiCsvGenerator {

    @Inject private ReportDao reportDao;

    private final Integer optionMultiplier = 100;
    private final String NL = "\n";
    private final String DL = ",";
    private final String acquireType = "A - nakup";
    private Map<ReportDefinitions.SecType, String> secTypeMap = new HashMap<>();
    private Map<ReportDefinitions.TradeType, String> tradeTypeMap = new HashMap<>();
    private DateFormat df = new SimpleDateFormat("dd. MM. yyyy");
    private NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

    @PostConstruct
    private void init() {
        secTypeMap.put(ReportDefinitions.SecType.FUT, "01 - terminska pogodba");
        secTypeMap.put(ReportDefinitions.SecType.OPT, "03 - opcija in certifikat");
        tradeTypeMap.put(ReportDefinitions.TradeType.LONG, "običajni");
        tradeTypeMap.put(ReportDefinitions.TradeType.SHORT, "na kratko");
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
    }

    public String generate(Report report, Integer year, ReportDefinitions.TradeType tradeType) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        Calendar beginDate = ReportUtil.toBeginOfPeriod(cal, ReportDefinitions.StatisticsInterval.YEAR);
        cal.set(Calendar.YEAR, year + 1);
        Calendar endDate = ReportUtil.toBeginOfPeriod(cal, ReportDefinitions.StatisticsInterval.YEAR);
        List<Trade> trades = reportDao.getTradesBetweenDates(report, beginDate, endDate, tradeType);
        StringBuilder sb = new StringBuilder();
        if (ReportDefinitions.TradeType.SHORT.equals(tradeType)) {
            writeCsvHeaderShort(sb);
        } else if (ReportDefinitions.TradeType.LONG.equals(tradeType)) {
            writeCsvHeaderLong(sb);
        }
        int i = 0;
        for (Trade trade : trades) {
            if (!(ReportDefinitions.SecType.FUT.equals(trade.getSecType()) || ReportDefinitions.SecType.OPT.equals(trade.getSecType()))) {
                continue;
            }
            i++;
            writeTrade(sb, trade, i);
            List<SplitExecution> splitExecutions = trade.getSplitExecutions();
            Collections.reverse(splitExecutions);
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
        }
        return sb.toString();
    }

    private void writeCsvHeaderShort(StringBuilder sb) {
        sb.append("Zap.").append(NL).append("št.").append(DL);
        sb.append("Vrsta IFI").append(DL);
        sb.append("Vrsta posla").append(DL);
        sb.append("Trgovalna").append(NL).append("koda").append(DL);
        sb.append("Datum").append(NL).append("odsvojitve").append(DL);
        sb.append("Količina").append(NL).append("odsvojenega").append(NL).append("IFI").append(DL);
        sb.append("Vrednost").append(NL).append("ob odsvojitvi").append(NL).append("(na enoto)").append(NL).append("USD").append(DL);
        sb.append("Vrednost").append(NL).append("ob odsvojitvi").append(NL).append("(na enoto)").append(NL).append("EUR").append(DL);
        sb.append("Datum").append(NL).append("pridobitve").append(DL);
        sb.append("Način").append(NL).append("pridobitve").append(DL);
        sb.append("Količina").append(DL);
        sb.append("Vrednost").append(NL).append("ob pridobitvi").append(NL).append("(na enoto)").append(NL).append("USD").append(DL);
        sb.append("Vrednost").append(NL).append("ob pridobitvi").append(NL).append("(na enoto)").append(NL).append("EUR").append(DL);
        sb.append("Zaloga").append(NL).append("IFI").append(DL);
        sb.append("Dobiček").append(NL).append("Izguba").append(NL).append("EUR").append(NL);
    }

    private void writeCsvHeaderLong(StringBuilder sb) {
        sb.append("Zap.").append(NL).append("št.").append(DL);
        sb.append("Vrsta IFI").append(DL);
        sb.append("Vrsta posla").append(DL);
        sb.append("Trgovalna").append(NL).append("koda").append(DL);
        sb.append("Datum").append(NL).append("pridobitve").append(DL);
        sb.append("Način").append(NL).append("pridobitve").append(DL);
        sb.append("Količina").append(DL);
        sb.append("Nabavna").append(NL).append("vrednost").append(NL).append("ob pridobitvi").append(NL).append("(na enoto)").append(NL).append("USD").append(DL);
        sb.append("Nabavna").append(NL).append("vrednost").append(NL).append("ob pridobitvi").append(NL).append("(na enoto)").append(NL).append("EUR").append(DL);
        sb.append("Datum").append(NL).append("odsvojitve").append(DL);
        sb.append("Količina").append(NL).append("odsvojenega").append(NL).append("IFI").append(DL);
        sb.append("Vrednost").append(NL).append("ob odsvojitvi").append(NL).append("(na enoto)").append(NL).append("USD").append(DL);
        sb.append("Vrednost").append(NL).append("ob odsvojitvi").append(NL).append("(na enoto)").append(NL).append("EUR").append(DL);
        sb.append("Zaloga").append(NL).append("IFI").append(DL);
        sb.append("Dobiček").append(NL).append("Izguba").append(NL).append("EUR").append(NL);
    }

    private void writeTrade(StringBuilder sb, Trade trade, int i) {
        sb.append(i).append(DL);
        sb.append(secTypeMap.get(trade.getSecType())).append(DL);
        sb.append(tradeTypeMap.get(trade.getType())).append(DL);
        sb.append(trade.getSymbol()).append(DL);
        for (int k = 0; k < 10; k++) {
            sb.append(DL);
        }
        sb.append(nf.format(trade.getProfitLoss()));
        sb.append(NL);
    }

    private void writeTradeShortSplitExecutionSell(StringBuilder sb, SplitExecution se, int i, int j) {
        ExchangeRate exchangeRate = reportDao.getExchangeRate(se.getFillDate());
        sb.append(i).append("-").append(j).append(DL).append(DL).append(DL);
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
        ExchangeRate exchangeRate = reportDao.getExchangeRate(se.getFillDate());
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
        ExchangeRate exchangeRate = reportDao.getExchangeRate(se.getFillDate());
        sb.append(i).append("-").append(j).append(DL).append(DL).append(DL);
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
        ExchangeRate exchangeRate = reportDao.getExchangeRate(se.getFillDate());
        sb.append(i).append("-");
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
