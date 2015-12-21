package com.highpowerbear.hpbanalytics.c2.persistence;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.entity.PollEvent;
import com.highpowerbear.hpbanalytics.c2.rest.model.C2SignalFilter;
import com.highpowerbear.hpbanalytics.c2.rest.model.InputRequestFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@Named
@Stateless
public class C2DaoImpl implements C2Dao {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @PersistenceContext(unitName = "hpbanalytics-PU")
    private EntityManager em;
    @Inject private QueryBuilder queryBuilder;

    private final String B = "BEGIN " + this.getClass().getSimpleName() + ".";
    private final String E = "END " + this.getClass().getSimpleName() + ".";

    @Override
    public C2System getC2SystemByOriginAndSecType(String origin, C2Definitions.SecType secType) {
        boolean stk = C2Definitions.SecType.STK.equals(secType);
        boolean opt = C2Definitions.SecType.OPT.equals(secType);
        boolean fut = C2Definitions.SecType.FUT.equals(secType);
        boolean fx =  C2Definitions.SecType.CASH.equals(secType);

        TypedQuery<C2System> q = em.createQuery("SELECT s FROM C2System s WHERE s.origin = :origin" +
                (stk ? " AND s.stk IS TRUE" : "") +
                (opt ? " AND s.opt IS TRUE" : "") +
                (fut ? " AND s.fut IS TRUE" : "") +
                (fx  ? " AND s.fx  IS TRUE" : ""), C2System.class);

        q.setParameter("origin", origin);
        List<C2System> list = q.getResultList();
        return (!list.isEmpty() ? list.get(0) : null);
    }

    @Override
    public C2System findC2System(Integer systemId) {
        return em.find(C2System.class, systemId);
    }

    @Override
    public List<C2System> getC2Systems() {
        TypedQuery<C2System> q = em.createQuery("SELECT s FROM C2System s", C2System.class);
        return q.getResultList();
    }

    @Override
    public C2System updateC2System(C2System c2System) {
        return em.merge(c2System);
    }

    @Override
    public void newC2Signal(C2Signal c2Signal) {
        l.info(B + "newC2Signal, dbId=" + c2Signal.getId() + ", signalId=" + c2Signal.getC2SignalId());
        em.persist(c2Signal);
        l.info(E + "newC2Signal, dbId=" + c2Signal.getId() + ". signalId=" + c2Signal.getC2SignalId());
    }

    @Override
    public C2Signal updateC2Signal(C2Signal c2Signal) {
        l.info(B + "updateC2Signal, signalId=" + c2Signal.getC2SignalId());
        c2Signal = em.merge(c2Signal);
        l.info(E + "updateC2Signal, signalId=" + c2Signal.getC2SignalId());
        return c2Signal;
    }

    @Override
    public C2Signal findC2Signal(Long dbId) {
        return em.find(C2Signal.class, dbId);
    }

    @Override
    public List<C2Signal> getC2SignalsToPoll(C2System c2System) {
        TypedQuery<C2Signal> q = em.createQuery("SELECT cs FROM C2Signal cs WHERE cs.c2System = :c2System AND cs.c2SignalId IS NOT NULL AND cs.pollStatus IN :statuses", C2Signal.class);
        q.setParameter("c2System", c2System);
        Set<C2Definitions.PollStatus> statuses = new HashSet<>();
        statuses.add(C2Definitions.PollStatus.NOTPOLLED);
        statuses.add(C2Definitions.PollStatus.WORKING);
        statuses.add(C2Definitions.PollStatus.POLLERR);
        q.setParameter("statuses", statuses);
        return q.getResultList();
    }

    @Override
    public List<C2Signal> getC2SignalsByOriginReference(String origin, String referenceId) {
        TypedQuery<C2Signal> q = em.createQuery("SELECT cs FROM C2Signal cs WHERE cs.origin = :origin AND cs.referenceId = :referenceId ORDER BY cs.createdDate ASC", C2Signal.class);
        q.setParameter("origin", origin);
        q.setParameter("referenceId", referenceId);
        return q.getResultList();
    }

    @Override
    public List<C2Signal> getFilteredC2Signals(C2System c2System, C2SignalFilter filter, Integer start, Integer limit) {
        Query q = queryBuilder.buildFilteredC2SignalsQuery(em, c2System, filter, false);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumFilteredC2Signals(C2System c2System, C2SignalFilter filter) {
        Query q = queryBuilder.buildFilteredC2SignalsQuery(em, c2System, filter, true);
        return (Long) q.getSingleResult();
    }

    @Override
    public void newPollEvent(PollEvent pollEvent) {
        em.persist(pollEvent);
        C2Signal c2Signal = pollEvent.getC2Signal();
        c2Signal.setPollStatus(pollEvent.getStatus());
        c2Signal.setPollDate(pollEvent.getEventDate());
        c2Signal.setTradePrice(pollEvent.getTradePrice());
        em.merge(c2Signal);
    }

    @Override
    public List<PollEvent> getPollEvents(Long c2SignalDbId, Integer start, Integer limit) {
        C2Signal c2Signal = em.find(C2Signal.class, c2SignalDbId);
        TypedQuery<PollEvent> q = em.createQuery("SELECT pe FROM PollEvent pe WHERE pe.c2Signal = :c2Signal ORDER BY pe.eventDate DESC", PollEvent.class);
        q.setParameter("c2Signal", c2Signal);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumPollEvents(Long c2SignaldbId) {
        C2Signal c2Signal = em.find(C2Signal.class, c2SignaldbId);
        Query q = em.createQuery("SELECT COUNT(pe) FROM PollEvent pe WHERE pe.c2Signal = :c2Signal");
        q.setParameter("c2Signal", c2Signal);
        return (Long) q.getSingleResult();
    }

    @Override
    public Long getPollErrorCount(C2Signal c2Signal) {
        Query q = em.createQuery("SELECT COUNT(pe) FROM PollEvent pe WHERE pe.c2Signal = :c2Signal AND pe.status = :status");
        q.setParameter("c2Signal", c2Signal);
        q.setParameter("status", C2Definitions.PollStatus.POLLERR);
        return (Long) q.getSingleResult();
    }

    @Override
    public void newInputRequest(InputRequest inputRequest) {
        l.info(B + "newInputRequest " + inputRequest.print());
        em.persist(inputRequest);
        l.info(E + "newInputRequest " + inputRequest.print());
    }

    @Override
    public void updateInputRequest(InputRequest inputRequest) {
        l.info(B + "updateInputRequest " + inputRequest.print());
        em.merge(inputRequest);
        l.info(E + "updateInputRequest " + inputRequest.print());
    }

    @Override
    public List<InputRequest> getFilteredInputRequests(InputRequestFilter filter, Integer start, Integer limit) {
        Query q = queryBuilder.buildFilteredInputRequestsQuery(em, filter, false);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumFilteredInputRequests(InputRequestFilter filter) {
        Query q = queryBuilder.buildFilteredInputRequestsQuery(em, filter, true);
        return (Long) q.getSingleResult();
    }
}
