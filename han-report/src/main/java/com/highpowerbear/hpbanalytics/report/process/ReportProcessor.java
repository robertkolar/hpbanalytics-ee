package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.SplitExecution;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.OptionParseResult;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;
import com.highpowerbear.hpbanalytics.report.websocket.WebsocketController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author rkolar
 */
@ApplicationScoped
public class ReportProcessor {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private ReportDao reportDao;
    @Inject private WebsocketController websocketController;
    @Inject private StatisticsCalculator statisticsCalculator;
    
    public void analyzeAll(Report report) {
        l.info("START report processing for " + report.getReportName());
        reportDao.deleteAllTrades(report);
        List<Execution> executions = reportDao.getExecutions(report);
        if (executions.isEmpty()) {
            l.info("END report processing for " + report.getReportName() + ", no executions, skipping");
            return;
        }
        List<Trade> trades = analyze(executions);
        reportDao.createTrades(trades);
        statisticsCalculator.clearCache(report);
        websocketController.broadcastReportMessage("report analyzed");
        l.info("END report processing for " + report.getReportName());
    }

    public void deleteReport(Report report) {
        reportDao.deleteReport(report);
        statisticsCalculator.clearCache(report);
        websocketController.broadcastReportMessage("report deleted");
    }
    
    public void deleteExecution(Execution execution) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trades affected by execution: ").append(execution.print()).append("\n");
        List<Trade> tradesAffected = reportDao.getTradesAffectedByExecution(execution);
        for (Trade trade : tradesAffected) {
            sb.append("Trade: ").append(trade.print()).append("\n");
            for (SplitExecution se : trade.getSplitExecutions()) {
                sb.append(se.print()).append("\n");
            }
        }
        l.info(sb.toString());
        reportDao.deleteTrades(tradesAffected);
        reportDao.deleteExecution(execution);
        SplitExecution firstSe = tradesAffected.get(0).getSplitExecutions().get(0);
        boolean isCleanCut = (firstSe.getSplitQuantity().equals(firstSe.getExecution().getQuantity()));
        boolean omitFirstSe = (isCleanCut && !reportDao.existsExecution(firstSe.getExecution())); // cleanCut is redundant
        List<Execution> executionsToAnalyzeAgain = reportDao.getExecutionsAfterExecution(firstSe.getExecution());
        List<Trade> newTrades = analyzeSingleSymbol(executionsToAnalyzeAgain, (omitFirstSe ? null : firstSe));
        if (!newTrades.isEmpty()) {
            reportDao.createTrades(newTrades);
        }
        statisticsCalculator.clearCache(execution.getReport());
        websocketController.broadcastReportMessage("execution deleted");
    }
    
    public Long newExecution(Execution execution) {
        List<Trade> tl = reportDao.getTradesAffectedByExecution(execution);
        StringBuilder sb = new StringBuilder();
        sb.append("Trades affected by execution: ").append(execution.print()).append("\n");
        for (Trade t : tl) {
           sb.append("Trade: ").append(t.print()).append("\n");
            for (SplitExecution se : t.getSplitExecutions()) {
                sb.append("Split execution: ").append(se.print()).append("\n");
            }
        }
        l.info(sb.toString());
        l.info("Deleting " + tl.size() + " trades");
        reportDao.deleteTrades(tl);
        reportDao.createExecution(execution);
        // refresh from db
        execution = reportDao.findExecution(execution.getId());
        List<Execution> executionsToAnalyzeAgain = new ArrayList<>();
        List<Trade> trades;
        if (!tl.isEmpty()) {
            SplitExecution firstSe = tl.get(0).getSplitExecutions().get(0);
            l.info("firstSe=" + firstSe.print());
            boolean isNewAfterFirst = execution.getFillDate().after(firstSe.getExecution().getFillDate());
            l.info("isNewAfterFirst=" + isNewAfterFirst + ", " + execution.getFillDate().getTime() + ", " + firstSe.getExecution().getFillDate().getTime());
            executionsToAnalyzeAgain = (isNewAfterFirst ? reportDao.getExecutionsAfterExecution(firstSe.getExecution()) : reportDao.getExecutionsAfterExecutionInclusive(execution));
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, (isNewAfterFirst ? firstSe : null));
        } else {
            executionsToAnalyzeAgain.add(execution);
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, null);
        }
        l.info("Creating " + trades.size() + " trades");
        reportDao.createTrades(trades);
        statisticsCalculator.clearCache(execution.getReport());
        websocketController.broadcastReportMessage("new execution processed");
        return execution.getId();
    }

    public Execution closeTrade(Trade trade, Calendar closeDate, BigDecimal closePrice) {
        Execution e = new Execution();
        e.setReceivedDate(Calendar.getInstance());
        e.setReport(trade.getReport());
        e.setComment(ReportDefinitions.CLOSE_TRADE_COMMENT);
        e.setOrigin(ReportDefinitions.ORIGIN_INTERNAL);
        e.setReferenceId(ReportDefinitions.NOT_AVAILABLE);
        e.setAction(trade.getType() == ReportDefinitions.TradeType.LONG ? ReportDefinitions.Action.SELL : ReportDefinitions.Action.BUY);
        e.setQuantity(Math.abs(trade.getOpenPosition()));
        e.setSymbol(trade.getSymbol());
        e.setUnderlying(trade.getUnderlying());
        e.setCurrency(trade.getCurrency());
        e.setSecType(trade.getSecType());
        e.setFillDate(closeDate);
        e.setFillPrice(closePrice);
        this.newExecution(e);
        return e;
    }

    public Execution expireTrade(Trade trade) {
        OptionParseResult opr;
        try {
            opr = OptionUtil.parse(trade.getSymbol());
        } catch (Exception e) {
            l.log(Level.SEVERE, "Error", e);
            return null;
        }
        Execution e = new Execution();
        e.setReceivedDate(Calendar.getInstance());
        e.setReport(trade.getReport());
        e.setComment(ReportDefinitions.EXPIRE_TRADE_COMMENT);
        e.setOrigin(ReportDefinitions.ORIGIN_INTERNAL);
        e.setReferenceId(ReportDefinitions.NOT_AVAILABLE);
        e.setAction(trade.getType() == ReportDefinitions.TradeType.LONG ? ReportDefinitions.Action.SELL : ReportDefinitions.Action.BUY);
        e.setQuantity(Math.abs(trade.getOpenPosition()));
        e.setSymbol(trade.getSymbol());
        e.setUnderlying(trade.getUnderlying());
        e.setCurrency(trade.getCurrency());
        e.setSecType(trade.getSecType());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(opr.getExpDate().getTime());
        e.setFillDate(cal);
        e.setFillPrice(new BigDecimal(0.0));
        this.newExecution(e);
        return e;
    }

    public List<Execution> assignTrade(Trade trade) {
        OptionParseResult opr;
        try {
            opr = OptionUtil.parse(trade.getSymbol());
        } catch (Exception exception) {
            l.log(Level.SEVERE, "Error", exception);
            return null;
        }
        List<Execution> list = new ArrayList<>();
        Execution e = new Execution();
        e.setReceivedDate(Calendar.getInstance());
        e.setReport(trade.getReport());
        e.setComment(ReportDefinitions.ASSIGN_TRADE_COMMENT);
        e.setOrigin(ReportDefinitions.ORIGIN_INTERNAL);
        e.setReferenceId(ReportDefinitions.NOT_AVAILABLE);
        e.setAction(trade.getType() == ReportDefinitions.TradeType.LONG ? ReportDefinitions.Action.SELL : ReportDefinitions.Action.BUY);
        e.setQuantity(Math.abs(trade.getOpenPosition()));
        e.setSymbol(trade.getSymbol());
        e.setUnderlying(trade.getUnderlying());
        e.setCurrency(trade.getCurrency());
        e.setSecType(trade.getSecType());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(opr.getExpDate().getTime());
        e.setFillDate(cal);
        e.setFillPrice(new BigDecimal(0.0));
        this.newExecution(e);

        Execution ce = new Execution();
        ce.setReceivedDate(e.getReceivedDate());
        ce.setReport(trade.getReport());
        ce.setComment(ReportDefinitions.ASSIGN_TRADE_COMMENT);
        ce.setOrigin(ReportDefinitions.ORIGIN_INTERNAL);
        ce.setReferenceId(ReportDefinitions.NOT_AVAILABLE);
        ce.setAction(ReportDefinitions.OptionType.PUT.equals(opr.getOptType()) ? ReportDefinitions.Action.BUY : ReportDefinitions.Action.SELL);
        ce.setQuantity((OptionUtil.isMini(e.getSymbol()) ? 10 : 100) * e.getQuantity());
        ce.setSymbol(e.getUnderlying());
        ce.setUnderlying(e.getUnderlying());
        ce.setCurrency(e.getCurrency());
        ce.setSecType(ReportDefinitions.SecType.STK);
        // introduce random offset for stocks that were purchased/sold as a result of assignment so in case of same symbol they don't get exactly the same date
        // this is required constraint for all executions for the same symbol and execution source, see Execution entity
        Random r = new Random();
        long randomLong = r.nextInt(59000);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(cal.getTimeInMillis() + randomLong);
        ce.setFillDate(cal1);
        ce.setFillPrice(new BigDecimal(opr.getStrikePrice()));
        this.newExecution(ce);

        list.add(e);
        list.add(ce);

        return list;
    }

    private List<Trade> analyze(List<Execution> executions) {
        return createTrades(createSplitExecutions(executions));
    }
    
    private List<Trade> analyzeSingleSymbol(List<Execution> executions, SplitExecution firstSe) {
        if (firstSe != null) {
            firstSe.setId(null);
            firstSe.setTrade(null);
        }
        return createTradesSingleSymbol(createSesSingleSymbol(executions, firstSe));
    }
    
    
    private List<SplitExecution> createSplitExecutions(List<Execution> executions) {
        List<SplitExecution> splitExecutions = new ArrayList<>();
        Set<String> symbols = executions.stream().map(Execution::getSymbol).collect(Collectors.toSet());
        Map<String, List<Execution>> mapExecutions = new HashMap<>();
        for (String s : symbols) {
            mapExecutions.put(s, new ArrayList<>());
        }
        for (Execution e : executions) {
            mapExecutions.get(e.getSymbol()).add(e);
        }
        for (String s : symbols) {
            splitExecutions.addAll(createSesSingleSymbol(mapExecutions.get(s), null));
        }
        return splitExecutions;
    }
   
    private List<SplitExecution> createSesSingleSymbol(List<Execution> executions, SplitExecution firstSe) {
        List<SplitExecution> sesSingleSymbol = new ArrayList<>();
        int currentPos = (firstSe != null ? firstSe.getCurrentPosition() : 0);
        if (firstSe != null) {
            sesSingleSymbol.add(firstSe);
        }
        for (Execution e : executions) {
            int ePos = (e.getAction() == ReportDefinitions.Action.BUY ? e.getQuantity() : -e.getQuantity());
            int newPos = currentPos + ePos;
            SplitExecution se;
            if (currentPos < 0 && newPos > 0) {
                // split
                se = new SplitExecution(); // first
                se.setExecution(e);
                se.setFillDate(e.getFillDate());
                se.setSplitQuantity(-currentPos);
                se.setCurrentPosition(0);
                sesSingleSymbol.add(se); 
                se = new SplitExecution(); //second
                se.setExecution(e);
                se.setFillDate(e.getFillDate());
                se.setSplitQuantity(newPos);
                se.setCurrentPosition(newPos);
                sesSingleSymbol.add(se);
            } else if (currentPos > 0 && newPos < 0) {
                // split
                se = new SplitExecution(); // first
                se.setExecution(e);
                se.setFillDate(e.getFillDate());
                se.setSplitQuantity(currentPos);
                se.setCurrentPosition(0);
                sesSingleSymbol.add(se);
                se = new SplitExecution(); //second
                se.setExecution(e);
                se.setFillDate(e.getFillDate());
                se.setSplitQuantity(-newPos);
                se.setCurrentPosition(newPos);
                sesSingleSymbol.add(se);
            } else {
                // normal
                se = new SplitExecution();
                se.setExecution(e);
                se.setFillDate(e.getFillDate());
                se.setSplitQuantity(e.getQuantity());
                se.setCurrentPosition(newPos);
                sesSingleSymbol.add(se);
            }
            currentPos = newPos;
        }
        return sesSingleSymbol;
    }
    
    private List<Trade> createTrades(List<SplitExecution> splitExecutions) {
        List<Trade> trades = new ArrayList<>();
        Set<String> symbols = splitExecutions.stream().map(se -> se.getExecution().getSymbol()).collect(Collectors.toSet());
        Map<String, List<SplitExecution>> mapSe = new HashMap<>();
        for (String s : symbols) {
            mapSe.put(s, new ArrayList<>());
        }
        for (SplitExecution se : splitExecutions) {
            mapSe.get(se.getExecution().getSymbol()).add(se);
        }
        for (String s : symbols) {
            trades.addAll(createTradesSingleSymbol(mapSe.get(s)));
        }
        return trades;
    }
    
    private List<Trade> createTradesSingleSymbol(List<SplitExecution> splitExecutions) {
        List<Trade> trades = new ArrayList<>();
        Set<SplitExecution> singleSymbolSet = new LinkedHashSet<>();
        singleSymbolSet.addAll(splitExecutions);
        while (!singleSymbolSet.isEmpty()) {
            Set<SplitExecution> singleTradeSet = new LinkedHashSet<>();
            Trade trade = new Trade();
            trade.setStatus(ReportDefinitions.TradeStatus.OPEN);
            for (SplitExecution se : singleSymbolSet) {
                singleTradeSet.add(se);
                if (se.getCurrentPosition() == 0) {
                    trade.setStatus(ReportDefinitions.TradeStatus.CLOSED);
                    break;
                }
            }
            trade.setSplitExecutions(new ArrayList<>(singleTradeSet));
            trade.calculate();
            trades.add(trade);
            singleSymbolSet.removeAll(singleTradeSet);
        }
        return trades;
    }
}
