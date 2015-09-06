package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.SplitExecution;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;

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
public class RepProcessor implements Serializable  {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(RepDefinitions.LOGGER);
    @Inject private RepDao repDao;
    
    public void analyzeAll(Report report) {
        l.info("START analytics processing for " + report.getName());
        repDao.deleteAllTrades(report);
        List<Execution> executions = repDao.getExecutions(report);
        if (executions.isEmpty()) {
            l.info("END analytics processing for " + report.getName() + ", no executions, skipping");
            return;
        }
        List<Trade> trades = analyze(executions);
        repDao.createTrades(trades);
        l.info("END analytics processing for " + report.getName());
    }
    
    public void deleteSelectedEcecutions(Execution[] selectedExecutions) {
        Set<String> symbols = new HashSet<>();
        for (Execution selectedExecution : selectedExecutions) {
            symbols.add(selectedExecution.getSymbol());
            l.info("Symbol: " + selectedExecution.getSymbol());
        }
        Map<String, List<Execution>> mapExecution = new HashMap<>();
        for (String s : symbols) {
            mapExecution.put(s, new ArrayList<>());
        }
        for (Execution selectedExecution : selectedExecutions) {
            mapExecution.get(selectedExecution.getSymbol()).add(selectedExecution);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SORTED:\n");
        for (String s : symbols) {
            Collections.sort(mapExecution.get(s));
            sb.append("Symbol: ").append(s).append("\n");
            for (Execution e : mapExecution.get(s)) {
                sb.append(e.print()).append("\n");
            }
        }
        l.info(sb.toString());
        sb = new StringBuilder();
        Map<String, List<Trade>> mapTrades = new HashMap<>();
        for (String s : symbols) {
            sb.append("Trades affected by execution: ").append(mapExecution.get(s).get(0).print()).append("\n");
            List<Trade> tl = repDao.getTradesAffectedByExecution(mapExecution.get(s).get(0));
            mapTrades.put(s, tl);
            for (Trade t : tl) {
                sb.append("Trade: ").append(t.print()).append("\n");
                for (SplitExecution se : t.getSplitExecutions()) {
                    sb.append(se.print()).append("\n");
                }
            }
        }
        l.info(sb.toString());
        for (String s : symbols) {
            List<Trade> tradesToDelete = mapTrades.get(s);
            List<Execution> executionsToDelete = mapExecution.get(s);
            repDao.deleteTrades(tradesToDelete);
            repDao.deleteExecutions(executionsToDelete);
            SplitExecution firstSe = tradesToDelete.get(0).getSplitExecutions().get(0);
            boolean isCleanCut = (firstSe.getSplitQuantity().equals(firstSe.getExecution().getQuantity()));
            boolean omitFirstSe = (isCleanCut && !repDao.existsExecution(firstSe.getExecution())); // cleanCut is redundant
            List<Execution> executionsToAnalyzeAgain = repDao.getExecutionsAfterExecution(firstSe.getExecution());
            List<Trade> tl = analyzeSingleSymbol(executionsToAnalyzeAgain, (omitFirstSe ? null : firstSe));
            if (!tl.isEmpty()) {
                repDao.createTrades(tl);
            }
        }
    }
    
    public void newExecution(Execution e) {
        List<Trade> tl = repDao.getTradesAffectedByExecution(e);
        StringBuilder sb = new StringBuilder();
        sb.append("Trades affected by execution: ").append(e.print()).append("\n");
        for (Trade t : tl) {
           sb.append("Trade: ").append(t.print()).append("\n");
            for (SplitExecution se : t.getSplitExecutions()) {
                sb.append("Split executon: ").append(se.print()).append("\n");
            }
        }
        l.info(sb.toString());
        repDao.deleteTrades(tl);
        repDao.createExecution(e);
        List<Execution> executionsToAnalyzeAgain = new ArrayList<>();
        List<Trade> trades;
        if (!tl.isEmpty()) {
            SplitExecution firstSe = tl.get(0).getSplitExecutions().get(0);
            boolean isNewAfterFirst = e.getFillDate().after(firstSe.getExecution().getFillDate());
            executionsToAnalyzeAgain = (isNewAfterFirst ? repDao.getExecutionsAfterExecution(firstSe.getExecution()) : repDao.getExecutionsAfterExecutionInclusive(e));
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, (isNewAfterFirst ? firstSe : null));
        } else {
            executionsToAnalyzeAgain.add(e);
            trades = analyzeSingleSymbol(executionsToAnalyzeAgain, null);
        }
        repDao.createTrades(trades);
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
            int ePos = (e.getAction() == RepDefinitions.Action.BUY ? e.getQuantity() : -e.getQuantity());
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
            trade.setStatus(RepDefinitions.TradeStatus.OPEN);
            for (SplitExecution se : singleSymbolSet) {
                singleTradeSet.add(se);
                if (se.getCurrentPosition() == 0) {
                    trade.setStatus(RepDefinitions.TradeStatus.CLOSED);
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
