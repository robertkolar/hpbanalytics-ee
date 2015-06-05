package com.highpowerbear.hpbanalytics.report.rest;

import com.highpowerbear.hpbanalytics.report.cdibean.StatisticsBean;
import com.highpowerbear.hpbanalytics.report.model.Statistics;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robertk
 */
@Path("report")
@ApplicationScoped
public class RepService {
    @Inject StatisticsBean statisticsBean;

    @GET
    @Path("statistics")
    @Produces("application/json")
    public List<Statistics> getStatistics() {
        return (statisticsBean.getStats() != null ? statisticsBean.getStats() : new ArrayList<>());
    }
}