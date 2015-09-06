package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.common.RepUtil;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/26/15.
 */
@Named
@ApplicationScoped
public class StatisticsCalculator implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger l = Logger.getLogger(RepDefinitions.LOGGER);

    @Inject private RepDao repDao;

    public List<Statistics> calculateStats(Report report, RepDefinitions.StatisticsInterval interval, String underlying) {
        l.info("START statistics calculation for " + report.getName() + ", undl=" + underlying + ", interval=" + interval);
        List<Trade> trades = repDao.getTrades(report, underlying);
        List<Statistics> stats = calculateStatistics(trades, interval);
        l.info("END statistics statistics calculation for " + report.getName() + ", interval=" + interval);
        return stats;
    }

    private List<Statistics> calculateStatistics(List<Trade> trades, RepDefinitions.StatisticsInterval interval) {
        List<Statistics> stats = new ArrayList<>();
        if (trades == null || trades.isEmpty()) {
            return stats;
        }
        Calendar firstPeriodDate = RepUtil.toBeginOfPeriod(this.getFirstDate(trades), interval);
        Calendar lastPeriodDate = RepUtil.toBeginOfPeriod(this.getLastDate(trades), interval);
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
                if (t.getProfitLoss() >= 0.0) {
                    numWinners++;
                    winnersProfit += t.getProfitLoss();
                    if (t.getProfitLoss() > maxWinner) {
                        maxWinner = t.getProfitLoss();
                    }
                } else {
                    numLosers++;
                    losersLoss -= t.getProfitLoss();
                    if (t.getProfitLoss() < maxLoser) {
                        maxLoser = t.getProfitLoss();
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

            if (RepDefinitions.StatisticsInterval.DAY.equals(interval)) {
                periodDate.add(Calendar.DAY_OF_MONTH, +1);
            } else if (RepDefinitions.StatisticsInterval.MONTH.equals(interval)) {
                periodDate.add(Calendar.MONTH, +1);
            }
        }
        return stats;
    }

    private Calendar getFirstDate(List<Trade> trades) {
        Calendar firstDateOpened = trades.get(0).getDateOpened();
        for (Trade t: trades) {
            if (t.getDateOpened().before(firstDateOpened)) {
                firstDateOpened = t.getDateOpened();
            }
        }
        return firstDateOpened;
    }

    private Calendar getLastDate(List<Trade> trades) {
        Calendar lastDate;
        Calendar lastDateOpened = trades.get(0).getDateOpened();
        Calendar lastDateClosed = trades.get(0).getDateClosed();
        for (Trade t: trades) {
            if (t.getDateOpened().after(lastDateOpened)) {
                lastDateOpened = t.getDateOpened();
            }
        }
        for (Trade t: trades) {
            if (t.getDateClosed() != null && (lastDateClosed == null || t.getDateClosed().after(lastDateClosed))) {
                lastDateClosed = t.getDateClosed();
            }
        }
        lastDate = (lastDateClosed == null || lastDateOpened.after(lastDateClosed) ? lastDateOpened : lastDateClosed);
        return lastDate;
    }

    private int getNumTradesOpenedForPeriod(List<Trade> trades, Calendar periodDate, RepDefinitions.StatisticsInterval interval) {
        int count = 0;
        for (Trade t: trades) {
            if (RepUtil.toBeginOfPeriod(t.getDateOpened(), interval).getTimeInMillis() == periodDate.getTimeInMillis()) {
                count++;
            }
        }
        return count;
    }

    private List<Trade> getTradesClosedForPeriod(List<Trade> trades, Calendar periodDate, RepDefinitions.StatisticsInterval interval) {
        List<Trade> tradesClosed = new ArrayList<>();
        for (Trade t: trades) {
            if (t.getDateClosed() != null && RepUtil.toBeginOfPeriod(t.getDateClosed(), interval).getTimeInMillis() == periodDate.getTimeInMillis()) {
                tradesClosed.add(t);
            }
        }
        return tradesClosed;
    }
}
