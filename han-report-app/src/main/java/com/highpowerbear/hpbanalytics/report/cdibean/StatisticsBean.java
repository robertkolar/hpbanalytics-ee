package com.highpowerbear.hpbanalytics.report.cdibean;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;
import com.highpowerbear.hpbanalytics.report.process.StatisticsCalculator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robertk
 */
@Named
@SessionScoped
public class StatisticsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject private RepDao repDao;
    @Inject private StatisticsCalculator statisticsCalculator;
    private List<Statistics> stats;
    private Report report;
    private String underlying;
    private List<String> allUnderlyings;
    private final String ALL = "All";
    private String interval;
    private List<String> intervals;
    
    @PostConstruct
    public void init() {
        intervals = new ArrayList<>();
        for (RepDefinitions.StatisticsInterval si : RepDefinitions.StatisticsInterval.values()) {
            intervals.add(si.getName());
        }
        interval = RepDefinitions.StatisticsInterval.MONTH.getName();
    }
    
    public void setReport(Report report) {
        this.report = report;
        this.underlying = ALL;
        this.allUnderlyings = new ArrayList<>();
        this.allUnderlyings.add(ALL);
        this.allUnderlyings.addAll(repDao.getUnderlyings(report));
        recalculate();
    }
    
    public Report getReport() {
        return report;
    }

    public List<Statistics> getStats() {
        return stats;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }
    
    public List<String> getAllUnderlyings() {
        return allUnderlyings;
    }
    
    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<String> getIntervals() {
        return intervals;
    }
    
    public void recalculate() {
        if (ALL.equals(underlying)) {
            stats = statisticsCalculator.calculateStats(report, null, RepDefinitions.StatisticsInterval.getByName(interval));
        } else {
            stats = statisticsCalculator.calculateStats(report, underlying, RepDefinitions.StatisticsInterval.getByName(interval));
        }
    }
}
