package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.SplitExecution;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author rkolar
 */
@ApplicationScoped
public class ReportProcessor implements Serializable  {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);
    @Inject private ReportDao reportDao;
    
    public void analyzeAll(Report report) {
        l.info("START analytics processing for " + report.getName());
        reportDao.deleteAllTrades(report);
        List<Execution> executions = reportDao.getExecutions(report);
        if (executions.isEmpty()) {
            l.info("END analytics processing for " + report.getName() + ", no executions, skipping");
            return;
        }
        List<Trade> trades = analyze(executions);
        reportDao.createTrades(trades);
        l.info("END analytics processing for " + report.getName());
    }
    
    public void deleteEcecution(Execution execution) {
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
    }
    
    public void newExecution(Execution e) {
        List<Trade> tl = reportDao.getTradesAffectedByExecution(e);
        StringBuilder sb = new StringBuilder();
        sb.append("Trades affected by execution: ").append(e.print()).append("\n");
        for (Trade t : tl) {
           sb.append("Trade: ").append(t.print()).append("\n");
            for (SplitExecution se : t.getSplitExecutions()) {
                sb.append("Split executon: ").append(se.print()).append("\n");
            }
        }
        l.info(sb.toString());
        reportDao.deleteTrades(tl);
        reportDao.createExecution(e);
        List<Execution> executionsToAnalyzeAgain = new ArrayList<>();
        List<Trade> trades;
        if (!tl.isEmpty()) {
            SplitExecution firstSe = tl.get(0).getSplitExecutions().get(0);
            boolean isNewAfterFirst = e.getFillDate().after(firstSe.getExecution().getFillDate());
            executionsToAnalyzeAgain = (isNewAfterFirst ? reportDao.getExecutionsAfterExecution(firstSe.getExecution()) : reportDao.getExecutionsAfterExecutionInclusive(e));
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, (isNewAfterFirst ? firstSe : null));
        } else {
            executionsToAnalyzeAgain.add(e);
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, null);
        }
        reportDao.createTrades(trades);
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
        Set<String> symbols = new HashSet<>();
        for (Execution e : executions) {
            symbols.add(e.getSymbol());
        }
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
                se.setDateFilled(e.getFillDate());
                se.setSplitQuantity(-currentPos);
                se.setCurrentPosition(0);
                sesSingleSymbol.add(se); 
                se = new SplitExecution(); //second
                se.setExecution(e);
                se.setDateFilled(e.getFillDate());
                se.setSplitQuantity(newPos);
                se.setCurrentPosition(newPos);
                sesSingleSymbol.add(se);
            } else if (currentPos > 0 && newPos < 0) {
                // split
                se = new SplitExecution(); // first
                se.setExecution(e);
                se.setDateFilled(e.getFillDate());
                se.setSplitQuantity(currentPos);
                se.setCurrentPosition(0);
                sesSingleSymbol.add(se);
                se = new SplitExecution(); //second
                se.setExecution(e);
                se.setDateFilled(e.getFillDate());
                se.setSplitQuantity(-newPos);
                se.setCurrentPosition(newPos);
                sesSingleSymbol.add(se);
            } else {
                // normal
                se = new SplitExecution();
                se.setExecution(e);
                se.setDateFilled(e.getFillDate());
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
