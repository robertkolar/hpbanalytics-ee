package com.highpowerbear.hpbanalytics.report.cdibean;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;
import com.highpowerbear.hpbanalytics.report.process.RepProcessor;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Robert
 */
@Named
@SessionScoped
public class Index implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject private RepDao repDao;
    @Inject private RepProcessor repProcessor;
    @Inject private ExecutionBean executionBean;
    @Inject private StatisticsBean statisticsBean;
    @Inject private TradeBean tradeBean;
    private List<Report> reports;
    private Report currentReport;
    private List<Execution> recentExecutions;
    
    @PostConstruct
    public void init() {
        refresh();
    }

    public List<Report> getReports() {
        return reports;
    }

    public Report getCurrentReport() {
        return currentReport;
    }

    public void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
    }
    
    public void refresh() {
        reports = repDao.getReports();
        recentExecutions = repDao.getRecentExecutions(RepDefinitions.RECENT_EXECUTIONS_LIMIT);
    }
        
    public void deleteAnalytics() {
        repDao.deleteReport(currentReport);
        refresh();
    }

    public String showTabs(Report report) {
        currentReport = report;
        executionBean.setReport(report);
        tradeBean.setReport(report);
        statisticsBean.setReport(report);
        return "tabs";
    }
    
    public void analyzeAll() {
        repProcessor.analyzeAll(currentReport);
        refresh();
    }

    public List<Execution> getRecentExecutions() {
        return recentExecutions;
    }

    public Long getNumExecutions(Report report) {
        return repDao.getNumExecutions(report);
    }
    
    public Long getNumTrades(Report report) {
        return repDao.getNumTrades(report);
    }
    
    public Long getNumOpenTrades(Report report) {
        return repDao.getNumOpenTrades(report);
    }
    
    public Long getNumUnderlyings(Report report) {
        return repDao.getNumUnderlyings(report);
    }
    
    public Long getNumOpenUnderlyings(Report report) {
        return repDao.getNumOpenUnderlyings(report);
    }
    
    public Date getFirstExecutionDate(Report analytics) {
        return repDao.getFirstExecutionDate(analytics);
    }
    
    public Date getLastExecutionDate(Report analytics) {
        String millisString = repDao.getLastExecutionDate(analytics);
        return (millisString != null ? new Date(Long.valueOf(millisString)) : null);
    }
}
