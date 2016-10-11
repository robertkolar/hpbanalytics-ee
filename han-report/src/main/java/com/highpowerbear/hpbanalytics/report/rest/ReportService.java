package com.highpowerbear.hpbanalytics.report.rest;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.OptionParseResult;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;
import com.highpowerbear.hpbanalytics.report.process.IfiCsvGenerator;
import com.highpowerbear.hpbanalytics.report.process.OptionUtil;
import com.highpowerbear.hpbanalytics.report.process.ReportProcessor;
import com.highpowerbear.hpbanalytics.report.process.StatisticsCalculator;
import com.highpowerbear.hpbanalytics.report.rest.model.CloseTradeDto;
import com.highpowerbear.hpbanalytics.report.rest.model.ExecutionFilter;
import com.highpowerbear.hpbanalytics.report.rest.model.TradeFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 *
 * @author robertk
 */
@Path("report")
@ApplicationScoped
public class ReportService {

    @Inject ReportDao reportDao;
    @Inject private StatisticsCalculator statisticsCalculator;
    @Inject private ReportProcessor reportProcessor;
    @Inject private FilterParser filterParser;
    @Inject private IfiCsvGenerator ifiCsvGenerator;

    @GET
    @Path("reports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReports() {
        List<Report> reports = reportDao.getReports();
        for (Report r : reports) {
            r.setNumExecutions(reportDao.getNumExecutions(r));
            r.setNumTrades(reportDao.getNumTrades(r));
            r.setNumOpenTrades(reportDao.getNumOpenTrades(r));
            r.setNumUnderlyings(reportDao.getNumUnderlyings(r));
            r.setNumOpenUnderlyings(reportDao.getNumOpenUnderlyings(r));
            r.setFirstExecutionDate(reportDao.getFirstExecutionDate(r));
            r.setLastExecutionDate(reportDao.getLastExecutionDate(r));
        }
        return Response.ok(reports).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reports")
    public Response updateReport(Report report) {
        Report reportDb = reportDao.findReport(report.getId());
        if (reportDb == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reportDao.updateReport(report)).build();
    }

    @PUT
    @Path("reports/{id}")
    public Response analyzeReport(@PathParam("id") Integer id) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reportProcessor.analyzeAll(report);
        return Response.ok().build();
    }

    @DELETE
    @Path("reports/{id}")
    public Response deleteReport(@PathParam("id") Integer id) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reportProcessor.deleteReport(report);
        return Response.ok().build();
    }

    @GET
    @Path("reports/{id}/executions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilteredExecutions(
            @PathParam("id") Integer id,
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start == null ? 0 : start);
        limit = (limit == null ? ReportDefinitions.JPA_MAX_RESULTS : limit);
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ExecutionFilter filter = filterParser.parseExecutionFilter(jsonFilter);
        List<Execution> executions = reportDao.getFilteredExecutions(report, filter, start, limit);
        Long numExecutions = reportDao.getNumFilteredExecutions(report, filter);
        return Response.ok(new RestList<>(executions, numExecutions)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("reports/{id}/executions")
    public Response createExecution(@PathParam("id") Integer reportId, Execution execution) {
        Report report = reportDao.findReport(reportId);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        execution.setId(null);
        execution.setReport(report);
        // fix fill date timezone, JAXB JSON converter sets it to UTC
        execution.getFillDate().setTimeZone(TimeZone.getTimeZone(ReportDefinitions.TIMEZONE));
        execution.setReceivedDate(Calendar.getInstance());
        Long executionId = reportProcessor.newExecution(execution);
        execution.setId(executionId);
        return (executionId != null ? Response.ok(execution).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
    }

    @DELETE
    @Path("reports/{id}/executions/{executionid}")
    public Response deleteExecution(@PathParam("id") Integer reportId, @PathParam("executionid") Long executionId) {
        Execution execution = reportDao.findExecution(executionId);
        if (execution == null || !reportId.equals(execution.getReportId())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reportProcessor.deleteExecution(execution);
        return Response.ok().build();
    }

    @GET
    @Path("reports/{id}/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilteredTrades(
            @PathParam("id") Integer id,
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start == null ? 0 : start);
        limit = (limit == null ? ReportDefinitions.JPA_MAX_RESULTS : limit);
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        TradeFilter filter = filterParser.parseTradeFilter(jsonFilter);
        List<Trade> trades = reportDao.getFilteredTrades(report, filter, start, limit);
        Long numTrades = reportDao.getNumFilteredTrades(report, filter);
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    @PUT
    @Path("reports/{id}/trades/{tradeid}/close")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response closeTrade(@PathParam("id") Integer id, @PathParam("tradeid") Long tradeId, CloseTradeDto closeTradeDto) {
        Report report = reportDao.findReport(id);
        Trade trade = reportDao.findTrade(tradeId);
        if (report == null || trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!ReportDefinitions.TradeStatus.OPEN.equals(trade.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // fix fill date timezone, JAXB JSON converter sets it to UTC
        closeTradeDto.getCloseDate().setTimeZone(TimeZone.getTimeZone(ReportDefinitions.TIMEZONE));
        return Response.ok(reportProcessor.closeTrade(trade, closeTradeDto.getCloseDate(), closeTradeDto.getClosePrice())).build();
    }

    @PUT
    @Path("reports/{id}/trades/{tradeid}/expire")
    @Produces(MediaType.APPLICATION_JSON)
    public Response expireTrade(@PathParam("id") Integer id, @PathParam("tradeid") Long tradeId) {
        Report report = reportDao.findReport(id);
        Trade trade = reportDao.findTrade(tradeId);
        if (report == null || trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!ReportDefinitions.SecType.OPT.equals(trade.getSecType()) || !ReportDefinitions.TradeStatus.OPEN.equals(trade.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(reportProcessor.expireTrade(trade)).build();
    }

    @PUT
    @Path("reports/{id}/trades/{tradeid}/assign")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTrade(@PathParam("id") Integer id, @PathParam("tradeid") Long tradeId) {
        Report report = reportDao.findReport(id);
        Trade trade = reportDao.findTrade(tradeId);
        if (report == null || trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!ReportDefinitions.SecType.OPT.equals(trade.getSecType()) || !ReportDefinitions.TradeStatus.OPEN.equals(trade.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(reportProcessor.assignTrade(trade)).build();
    }

    @GET
    @Path("reports/{id}/statistics/{interval}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@PathParam("id") Integer id,
                                  @PathParam("interval") ReportDefinitions.StatisticsInterval interval,
                                  @QueryParam("underlying") String underlying,
                                  @QueryParam("start") Integer start,
                                  @QueryParam("limit") Integer limit) {

        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Statistics> statistics = statisticsCalculator.getStatistics(report, interval, underlying);
        Collections.reverse(statistics);
        List<Statistics> statisticsPage = new ArrayList<>();
        for (int i = 0; i < statistics.size(); i++) {
            if (i >= start && i < (start + limit)) {
                statisticsPage.add(statistics.get(i));
            }
        }
        return Response.ok(new RestList<>(statisticsPage, (long) statistics.size())).build();
    }

    @GET
    @Path("reports/{id}/charts/{interval}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCharts(@PathParam("id") Integer id,
                              @PathParam("interval") ReportDefinitions.StatisticsInterval interval,
                              @QueryParam("underlying") String underlying) {

        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Statistics> statistics = statisticsCalculator.getStatistics(report, interval, underlying);
        return Response.ok(new RestList<>(statistics, (long) statistics.size())).build();
    }

    @PUT
    @Path("reports/{id}/statistics/{interval}")
    public Response recalculateStatistics(@PathParam("id") Integer id, @PathParam("interval") ReportDefinitions.StatisticsInterval interval, @QueryParam("underlying") String underlying) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        statisticsCalculator.calculateStatistics(report, interval, underlying);
        return Response.ok().build();
    }

    @GET
    @Path("reports/{id}/underlyings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnderlyings(@PathParam("id") Integer id) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reportDao.getUnderlyings(report)).build();
    }

    @GET
    @Path("optionutil/parse")
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionUtilParse(@QueryParam("optionsymbol") String optionSymbol) {
        OptionParseResult optionParseResult;
        try {
            optionParseResult = OptionUtil.parse(optionSymbol);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(optionParseResult).build();
    }

    @GET
    @Path("reports/{id}/ificsv/{year}/{tradetype}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIfiCsv(@PathParam("id") Integer id, @PathParam("year") Integer year, @PathParam("tradetype") ReportDefinitions.TradeType tradeType) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ifiCsvGenerator.generate(report, year, tradeType)).build();
    }
}