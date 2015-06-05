package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;

import java.sql.Date;
import java.util.List;

/**
 * Created by robertk on 4/11/15.
 */
public interface RepDao {
    Date getFirstExecutionDate(Report report);
    String getLastExecutionDate(Report report);
    Long getNumExecutions(Report report);
    Report getReportByOrigin(String origin);
    void deleteReport(Report report);
    List<Report> getReports();
    Long getNumTrades(Report report);
    Long getNumOpenTrades(Report report);
    List<Trade> getTrades(Report report, boolean descending);
    List<Trade> getTrades(Report report, String underlying);
    List<Trade> getTradesAffectedByExecution(Execution e);
    void addTrades(List<Trade> trades);
    void deleteAllTrades(Report report);
    void deleteTrades(List<Trade> trades);
    List<Execution> getExecutions(Report report);
    List<Execution> getRecentExecutions(Integer limit);
    List<Execution> getExecutions(Report report, boolean descending);
    List<Execution> getExecutionsAfterExecution(Execution e);
    List<Execution> getExecutionsAfterExecutionInclusive(Execution e);
    boolean existsExecution(Execution e);
    void newExecution(Execution execution);
    void deleteExecutions(List<Execution> executions);
    List<String> getUnderlyings(Report report);
    Long getNumUnderlyings(Report report);
    Long getNumOpenUnderlyings(Report report);
}
