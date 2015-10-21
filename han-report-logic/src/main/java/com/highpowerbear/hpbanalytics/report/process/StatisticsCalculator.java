package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.common.ReportUtil;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by robertk on 4/26/15.
 */
@Named
@ApplicationScoped
public class StatisticsCalculator implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private ReportDao reportDao;
    private Map<String, List<Statistics>> statisticsMap = new HashMap<>(); // caching statistics to prevent excessive recalculation

    public void clearCache(Report report) {
        Map<String, List<Statistics>> shanpshotMap = new HashMap<>(statisticsMap);
        shanpshotMap.keySet().stream().filter(key -> key.startsWith(report.getId() + "_")).forEach(statisticsMap::remove);
    }

    public List<Statistics> getStatistics(Report report, ReportDefinitions.StatisticsInterval interval, String underlying) {
        if(statisticsMap.get(report.getId() + "_" + interval.name() + "_" + underlying) == null) {
            calculateStatistics(report, interval, underlying);
        }
        return statisticsMap.get(report.getId() + "_" + interval.name() + "_" + underlying);
    }

    public List<Statistics> calculateStatistics(Report report, ReportDefinitions.StatisticsInterval interval, String underlying) {
        l.info("START statistics calculation for " + report.getName() + ", undl=" + underlying + ", interval=" + interval);
        List<Trade> trades = reportDao.getTradesByUnderlying(report, underlying);
        List<Statistics> stats = doCalculate(trades, interval);
        statisticsMap.put(report.getId() + "_" + interval.name() + "_" + underlying, stats);
        l.info("END statistics statistics calculation for " + report.getName() + ", interval=" + interval);
        return stats;
    }

    private List<Statistics> doCalculate(List<Trade> trades, ReportDefinitions.StatisticsInterval interval) {
        List<Statistics> stats = new ArrayList<>();
        if (trades == null || trades.isEmpty()) {
            return stats;
        }
        Calendar firstPeriodDate = ReportUtil.toBeginOfPeriod(this.getFirstDate(trades), interval);
        Calendar lastPeriodDate = ReportUtil.toBeginOfPeriod(this.getLastDate(trades), interval);
        Calendar periodDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        periodDate.setTimeInMillis(firstPeriodDate.getTimeInMillis());
        double cummulProfitLoss = 0.0;
        int statsCount = 1;
        while (periodDate.getTimeInMillis() <= lastPeriodDate.getTimeInMillis()) {
            Statistics s = new Statistics(statsCount++);
            Calendar periodDateCopy = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
            periodDateCopy.setTimeInMillis(periodDate.getTimeInMillis());
            s.setPeriodDate(periodDateCopy);
            s.setNumOpened(this.getNumTradesOpenedForPeriod(trades, periodDate, interval));
            List<Trade> tradesClosedForPeriod = this.getTradesClosedForPeriod(trades, periodDate, interval);
            s.setNumClosed(tradesClosedForPeriod.size());
            int numWinners = 0;
            int numLosers = 0;
            double winnersProfit = 0.0;
            double losersLoss = 0.0;
            double maxWinner = 0.0;
            double maxLoser = 0.0;
            double profitLoss;

            for (Trade t : tradesClosedForPeriod) {
                if (t.getProfitLoss().doubleValue() >= 0.0) {
                    numWinners++;
                    winnersProfit += t.getProfitLoss().doubleValue();
                    if (t.getProfitLoss().doubleValue() > maxWinner) {
                        maxWinner = t.getProfitLoss().doubleValue();
                    }
                } else {
                    numLosers++;
                    losersLoss -= t.getProfitLoss().doubleValue();
                    if (t.getProfitLoss().doubleValue() < maxLoser) {
                        maxLoser = t.getProfitLoss().doubleValue();
                    }
                }
            }
            profitLoss = winnersProfit - losersLoss;
            cummulProfitLoss += profitLoss;
            s.setNumWinners(numWinners);
            s.setNumLosers(numLosers);
            s.setWinnersProfit(winnersProfit);
            s.setLosersLoss(losersLoss);
            s.setMaxWinner(maxWinner);
            s.setMaxLoser(maxLoser == 0.0 ? maxLoser : -maxLoser);
            s.setProfitLoss(profitLoss);
            s.setCumulProfitLoss(cummulProfitLoss);
            stats.add(s);

            if (ReportDefinitions.StatisticsInterval.DAY.equals(interval)) {
                periodDate.add(Calendar.DAY_OF_MONTH, +1);
            } else if (ReportDefinitions.StatisticsInterval.MONTH.equals(interval)) {
                periodDate.add(Calendar.MONTH, +1);
            }
        }
        return stats;
    }

    private Calendar getFirstDate(List<Trade> trades) {
        Calendar firstDateOpened = trades.get(0).getOpenDate();
        for (Trade t: trades) {
            if (t.getOpenDate().before(firstDateOpened)) {
                firstDateOpened = t.getOpenDate();
            }
        }
        return firstDateOpened;
    }

    private Calendar getLastDate(List<Trade> trades) {
        Calendar lastDate;
        Calendar lastDateOpened = trades.get(0).getOpenDate();
        Calendar lastDateClosed = trades.get(0).getCloseDate();
        for (Trade t: trades) {
            if (t.getOpenDate().after(lastDateOpened)) {
                lastDateOpened = t.getOpenDate();
            }
        }
        for (Trade t: trades) {
            if (t.getCloseDate() != null && (lastDateClosed == null || t.getCloseDate().after(lastDateClosed))) {
                lastDateClosed = t.getCloseDate();
            }
        }
        lastDate = (lastDateClosed == null || lastDateOpened.after(lastDateClosed) ? lastDateOpened : lastDateClosed);
        return lastDate;
    }

    private int getNumTradesOpenedForPeriod(List<Trade> trades, Calendar periodDate, ReportDefinitions.StatisticsInterval interval) {
        int count = 0;
        for (Trade t: trades) {
            if (ReportUtil.toBeginOfPeriod(t.getOpenDate(), interval).getTimeInMillis() == periodDate.getTimeInMillis()) {
                count++;
            }
        }
        return count;
    }

    private List<Trade> getTradesClosedForPeriod(List<Trade> trades, Calendar periodDate, ReportDefinitions.StatisticsInterval interval) {
        return trades.stream().filter(t -> t.getCloseDate() != null && ReportUtil.toBeginOfPeriod(t.getCloseDate(), interval).getTimeInMillis() == periodDate.getTimeInMillis()).collect(Collectors.toList());
    }
}
