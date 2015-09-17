package com.highpowerbear.hpbanalytics.report.rest;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;
import com.highpowerbear.hpbanalytics.report.process.ReportProcessor;
import com.highpowerbear.hpbanalytics.report.process.StatisticsCalculator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

    @GET
    @Path("reports/{id}/executions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExecutions(@PathParam("id") Integer id, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start == null ? 0 : start);
        limit = (limit == null ? ReportDefinitions.JPA_MAX_RESULTS : limit);
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Execution> executions = reportDao.getExecutions(report, start, limit);
        Long numExecutions = reportDao.getNumExecutions(report);
        return Response.ok(new RestList<>(executions, numExecutions)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("reports/{id}/executions")
    public Response createExecution(@PathParam("id") Integer reportId, Execution execution) {
        execution.setId(null);
        reportDao.createExecution(execution);
        return (execution.getId() != null ? Response.ok(execution).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("reports/{id}/executions/{executionid}")
    public Response deleteExecution(@PathParam("id") Integer reportId, @PathParam("executionid") Long executionId) {
        Execution execution = reportDao.findExecution(executionId);
        if (execution == null || !reportId.equals(execution.getReportId())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reportProcessor.deleteEcecution(execution);
        return Response.ok().build();
    }

    @GET
    @Path("reports/{id}/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(@PathParam("id") Integer id, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start == null ? 0 : start);
        limit = (limit == null ? ReportDefinitions.JPA_MAX_RESULTS : limit);
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Trade> trades = reportDao.getTrades(report, start, limit);
        Long numTrades = reportDao.getNumTrades(report);
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    @GET
    @Path("reports/{id}/statistics/{interval}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@PathParam("id") Integer id, @PathParam("interval") ReportDefinitions.StatisticsInterval interval, @QueryParam("underlying") String underlying) {
        Report report = reportDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Statistics> statistics = statisticsCalculator.calculateStats(report, interval, underlying);
        return Response.ok(new RestList<>(statistics, (long) statistics.size())).build();
    }
}