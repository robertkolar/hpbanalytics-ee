package com.highpowerbear.hpbanalytics.c2.rest;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.entity.PollEvent;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
import com.highpowerbear.hpbanalytics.c2.rest.model.C2SignalFilter;
import com.highpowerbear.hpbanalytics.c2.rest.model.InputRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@Path("c2")
@ApplicationScoped
public class C2Service {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @Inject private C2Dao c2Dao;
    @Inject private FilterParser filterParser;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("c2systems")
    public RestList<C2System> getC2Systems() {
        List<C2System> c2Systems = c2Dao.getC2Systems();
        return new RestList<>(c2Systems, (long) c2Systems.size());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("c2systems")
    public C2System updateC2System(C2System c2System) {
        C2System c2SystemDb = c2Dao.findC2System(c2System.getSystemId());
        if (c2SystemDb == null) {
            return null;
        }
        c2System.setPassword(c2SystemDb.getPassword());
        return c2Dao.updateC2System(c2System);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("c2systems/{systemId}/c2signals")
    public Response getC2Signals(
            @PathParam("systemId") Integer systemId,
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start != null ? start : 0);
        limit = (limit != null ? limit : C2Definitions.JPA_MAX_RESULTS);
        C2System c2System = c2Dao.findC2System(systemId);
        if (c2System == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        C2SignalFilter filter = filterParser.parseC2SignalFilter(jsonFilter);
        return Response.ok(new RestList<>(c2Dao.getFilteredC2Signals(c2System, filter, start, limit), c2Dao.getNumFilteredC2Signals(c2System, filter))).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("c2signals/{dbId}/pollevents")
    public RestList<PollEvent> getPollEvents(@PathParam("dbId") Long dbId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = (start != null ? start : 0);
        limit = (limit != null ? limit : C2Definitions.JPA_MAX_RESULTS);
        return new RestList<>(c2Dao.getPollEvents(dbId, start, limit), c2Dao.getNumPollEvents(dbId));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("inputrequests")
    public RestList<InputRequest> getInputRequests(
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start != null ? start : 0);
        limit = (limit != null ? limit : C2Definitions.JPA_MAX_RESULTS);
        InputRequestFilter filter = filterParser.parseInputRequestFilter(jsonFilter);
        return new RestList<>(c2Dao.getFilteredInputRequests(filter, start, limit), c2Dao.getNumFilteredInputRequests(filter));
    }
}