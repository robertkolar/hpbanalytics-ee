package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 4/11/15.
 */
public interface ReportDao {
    Report getReportByOrigin(String origin);
    Report findReport(Integer id);
    void deleteReport(Report report);
    List<Report> getReports();

    Calendar getFirstExecutionDate(Report report);
    Calendar getLastExecutionDate(Report report);
    List<Execution> getExecutions(Report report);
    Long getNumExecutions(Report report);
    List<Execution> getExecutions(Report report, Integer start, Integer limit);
    List<Execution> getExecutionsAfterExecution(Execution e);
    List<Execution> getExecutionsAfterExecutionInclusive(Execution e);
    boolean existsExecution(Execution e);
    Execution findExecution(Long id);
    void createExecution(Execution execution);
    void deleteExecution(Execution execution);

    Long getNumTrades(Report report);
    Long getNumOpenTrades(Report report);
    List<Trade> getTrades(Report report, Integer start, Integer limit);
    List<Trade> getTrades(Report report, String underlying);
    List<Trade> getTradesAffectedByExecution(Execution e);
    void createTrades(List<Trade> trades);
    void deleteAllTrades(Report report);
    void deleteTrades(List<Trade> trades);

    List<String> getUnderlyings(Report report);
    Long getNumUnderlyings(Report report);
    Long getNumOpenUnderlyings(Report report);
}
