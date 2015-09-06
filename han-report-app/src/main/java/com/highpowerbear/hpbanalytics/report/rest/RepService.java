package com.highpowerbear.hpbanalytics.report.rest;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;
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
public class RepService {
    @Inject RepDao repDao;
    @Inject private StatisticsCalculator statisticsCalculator;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReports() {
        List<Report> reports = repDao.getReports();
        for (Report r : reports) {
            r.setNumExecutions(repDao.getNumExecutions(r));
            r.setNumTrades(repDao.getNumTrades(r));
            r.setNumOpenTrades(repDao.getNumOpenTrades(r));
            r.setNumUnderlyings(repDao.getNumUnderlyings(r));
            r.setNumOpenUnderlyings(repDao.getNumOpenUnderlyings(r));
            r.setFirstExecutionDate(repDao.getFirstExecutionDate(r));
            r.setLastExecutionDate(repDao.getLastExecutionDate(r));
        }
        return Response.ok(reports).build();
    }

    @GET
    @Path("{id}/executions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExecutions(@PathParam("id") Integer id, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start == null ? 0 : start);
        limit = (limit == null ? RepDefinitions.JPA_MAX_RESULTS : limit);
        Report report = repDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Execution> executions = repDao.getExecutions(report, start, limit);
        Long numExecutions = repDao.getNumExecutions(report);
        return Response.ok(new RestList<>(executions, numExecutions)).build();
    }

    @GET
    @Path("{id}/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(@PathParam("id") Integer id, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start == null ? 0 : start);
        limit = (limit == null ? RepDefinitions.JPA_MAX_RESULTS : limit);
        Report report = repDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Trade> trades = repDao.getTrades(report, start, limit);
        Long numTrades = repDao.getNumTrades(report);
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    @GET
    @Path("{id}/statistics/{interval}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@PathParam("id") Integer id, @PathParam("interval") RepDefinitions.StatisticsInterval interval, @QueryParam("underlying") String underlying) {
        Report report = repDao.findReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Statistics> statistics = statisticsCalculator.calculateStats(report, interval, underlying);
        return Response.ok(statistics).build();
    }
}