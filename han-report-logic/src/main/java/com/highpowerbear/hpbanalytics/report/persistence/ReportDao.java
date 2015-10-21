package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.rest.model.ExecutionFilter;
import com.highpowerbear.hpbanalytics.report.rest.model.TradeFilter;

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
    List<Execution> getExecutionsAfterExecution(Execution e);
    List<Execution> getExecutionsAfterExecutionInclusive(Execution e);
    boolean existsExecution(Execution e);
    Execution findExecution(Long id);
    void createExecution(Execution execution);
    void deleteExecution(Execution execution);
    List<Execution> getFilteredExecutions(Report report, ExecutionFilter filter, Integer start, Integer limit);
    Long getNumFilteredExecutions(Report report, ExecutionFilter filter);

    Long getNumTrades(Report report);
    Long getNumOpenTrades(Report report);
    List<Trade> getTradesByUnderlying(Report report, String underlying);
    List<Trade> getTradesAffectedByExecution(Execution e);
    void createTrades(List<Trade> trades);
    void deleteAllTrades(Report report);
    void deleteTrades(List<Trade> trades);
    Trade findTrade(Long id);
    List<Trade> getFilteredTrades(Report report, TradeFilter filter, Integer start, Integer limit);
    Long getNumFilteredTrades(Report report, TradeFilter filter);

    List<String> getUnderlyings(Report report);
    Long getNumUnderlyings(Report report);
    Long getNumOpenUnderlyings(Report report);
}
