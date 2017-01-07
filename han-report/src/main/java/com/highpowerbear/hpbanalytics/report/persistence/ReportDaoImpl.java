package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.ExchangeRate;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.rest.model.ExecutionFilter;
import com.highpowerbear.hpbanalytics.report.rest.model.TradeFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Robert
 */
@Stateless
public class ReportDaoImpl implements ReportDao {

    @PersistenceContext
    private EntityManager em;
    @Inject private QueryBuilder queryBuilder;

    @Override
    public Report getReportByOriginAndSecType(String origin, ReportDefinitions.SecType secType) {
        boolean stk = ReportDefinitions.SecType.STK.equals(secType);
        boolean opt = ReportDefinitions.SecType.OPT.equals(secType);
        boolean fut = ReportDefinitions.SecType.FUT.equals(secType);
        boolean fx =  ReportDefinitions.SecType.CASH.equals(secType);
        boolean cfd =  ReportDefinitions.SecType.CFD.equals(secType);

        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r WHERE r.origin = :origin" +
                (stk ? " AND r.stk IS TRUE" : "") +
                (opt ? " AND r.opt IS TRUE" : "") +
                (fut ? " AND r.fut IS TRUE" : "") +
                (fx  ? " AND r.fx  IS TRUE" : "") +
                (cfd ? " AND r.cfd IS TRUE" : ""), Report.class);

        q.setParameter("origin", origin);
        List<Report> list = q.getResultList();
        return (!list.isEmpty() ? list.get(0) : null);
    }

    @Override
    public Report findReport(Integer id) {
        return em.find(Report.class, id);
    }

    @Override
    public Report updateReport(Report report) {
        return em.merge(report);
    }

    @Override
    public void deleteReport(Report report) {
        Report reportDb = em.find(Report.class, report.getId());
        this.deleteAllTrades(report);
        this.getExecutions(report).forEach(em::remove);
        em.remove(reportDb);
    }

    @Override
    public List<Report> getReports() {
        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r ORDER BY r.id", Report.class);
        return q.getResultList();
    }

    @Override
    public Calendar getFirstExecutionDate(Report report) {
        Query q = em.createQuery("SELECT MIN(e.fillDate) FROM Execution e WHERE e.report = :report");
        q.setParameter("report", report);
        return (Calendar) q.getResultList().get(0);
    }

    @Override
    public Calendar getLastExecutionDate(Report report) {
        Query q = em.createQuery("SELECT MAX(e.fillDate) FROM Execution e WHERE e.report = :report");
        q.setParameter("report", report);
        return  (Calendar) q.getResultList().get(0);
    }

    @Override
    public Long getNumExecutions(Report report) {
        Query query = em.createQuery("SELECT COUNT(e) FROM Execution e WHERE e.report = :report");
        query.setParameter("report", report);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Execution> getExecutions(Report report) {
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report = :report ORDER BY e.fillDate ASC", Execution.class);
        q.setParameter("report", report);
        return q.getResultList();
    }

    @Override
    public List<Execution> getExecutionsAfterExecution(Execution e) {
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report = :report AND e.fillDate > :eDate AND e.symbol = :eSymbol ORDER BY e.fillDate ASC", Execution.class);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        return q.getResultList();
    }

    @Override
    public List<Execution> getExecutionsAfterExecutionInclusive(Execution e) {
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report= :report AND e.fillDate >= :eDate AND e.symbol = :eSymbol ORDER BY e.fillDate ASC", Execution.class);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        return q.getResultList();
    }

    @Override
    public boolean existsExecution(Execution e) {
        Execution eDb = em.find(Execution.class, e.getId());
        return (eDb != null);
    }

    @Override
    public Execution findExecution(Long id) {
        return em.find(Execution.class, id);
    }

    @Override
    public void createExecution(Execution execution) {
        em.persist(execution);
    }

    @Override
    public void deleteExecution(Execution execution) {
        Execution executionDb = em.find(Execution.class, execution.getId());
        em.remove(executionDb);
    }

    @Override
    public List<Execution> getFilteredExecutions(Report report, ExecutionFilter filter, Integer start, Integer limit) {
        Query q = queryBuilder.buildFilteredExecutionsQuery(em , report, filter, false);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumFilteredExecutions(Report report, ExecutionFilter filter) {
        Query q = queryBuilder.buildFilteredExecutionsQuery(em , report, filter, true);
        return (Long) q.getSingleResult();
    }

    @Override
    public Long getNumTrades(Report report) {
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.report = :report");
        query.setParameter("report", report);
        return (Long) query.getSingleResult();
    }

    @Override
    public Long getNumOpenTrades(Report report) {
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.report = :report AND t.status = :tradeStatus");
        query.setParameter("report", report);
        query.setParameter("tradeStatus", ReportDefinitions.TradeStatus.OPEN);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Trade> getTradesByUnderlying(Report report, String underlying) {
        if (ReportDefinitions.ALL_UNDERLYINGS.equals(underlying)) {
            underlying = null;
        }
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report" +  (underlying != null ? " AND t.underlying = :underlying" : "") + " ORDER BY t.openDate ASC", Trade.class);
        q.setParameter("report", report);
        if (underlying != null) {
            q.setParameter("underlying", underlying);
        }
        List<Trade> list = q.getResultList();
        list.forEach(com.highpowerbear.hpbanalytics.report.entity.Trade::getSplitExecutions);
        return list;
    }

    @Override
    public List<Trade> getTradesAffectedByExecution(Execution e) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report AND (t.closeDate >= :eDate OR t.status = :tradeStatus) AND t.symbol = :eSymbol ORDER BY t.openDate ASC", Trade.class);
        q.setParameter("tradeStatus", ReportDefinitions.TradeStatus.OPEN);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        List<Trade> tl = q.getResultList();
        tl.forEach(com.highpowerbear.hpbanalytics.report.entity.Trade::getSplitExecutions);
        return tl;
    }

    @Override
    public void createTrades(List<Trade> trades) {
        trades.forEach(em::persist);
    }

    @Override
    public void deleteAllTrades(Report report) {
        for (Trade trade : this.getTradesByUnderlying(report, null)) {
            // it is managed, since trade is managed
            trade.getSplitExecutions().forEach(em::remove);
            em.remove(trade);
        }
    }

    @Override
    public void deleteTrades(List<Trade> trades) {
        if (trades == null) {
            return;
        }
        for (Trade trade : trades) {
            Trade tradeDb = em.find(Trade.class, trade.getId()); // make sure it is managed by entitymanager
            // it is managed, since trade is managed
            tradeDb.getSplitExecutions().forEach(em::remove);
            em.remove(tradeDb);
        }
    }

    @Override
    public Trade findTrade(Long id) {
        return em.find(Trade.class, id);
    }

    @Override
    public List<Trade> getFilteredTrades(Report report, TradeFilter filter, Integer start, Integer limit) {
        Query q = queryBuilder.buildFilteredTradesQuery(em, report, filter, false);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        List<Trade> list = q.getResultList();
        list.forEach(Trade::getSplitExecutions);
        return list;
    }

    @Override
    public Long getNumFilteredTrades(Report report, TradeFilter filter) {
        Query q = queryBuilder.buildFilteredTradesQuery(em, report, filter, true);
        return (Long) q.getSingleResult();
    }

    @Override
    public List<Trade> getTradesBetweenDates(Report report, Calendar beginDate, Calendar endDate, ReportDefinitions.TradeType tradeType) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report AND t.closeDate >= :beginDate AND t.closeDate < :endDate AND t.type = :tradeType ORDER BY t.openDate ASC", Trade.class);
        q.setParameter("report", report);
        q.setParameter("beginDate", beginDate);
        q.setParameter("endDate", endDate);
        q.setParameter("tradeType", tradeType);
        List<Trade> list = q.getResultList();
        list.forEach(Trade::getSplitExecutions);
        return list;
    }

    @Override
    public List<String> getUnderlyings(Report report) {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT e.underlying FROM Execution e WHERE e.report = :report", String.class);
        query.setParameter("report", report);
        return query.getResultList();
    }

    @Override
    public Long getNumUnderlyings(Report report) {
        Query query = em.createQuery("SELECT COUNT(DISTINCT e.underlying) FROM Execution e WHERE e.report = :report");
        query.setParameter("report", report);
        return (Long) query.getSingleResult();
    }

    @Override
    public Long getNumOpenUnderlyings(Report report) {
        Query query = em.createQuery("SELECT COUNT(DISTINCT se.execution.underlying) FROM SplitExecution se WHERE se.execution.report = :report AND se.trade.status = :tradeStatus");
        query.setParameter("report", report);
        query.setParameter("tradeStatus", ReportDefinitions.TradeStatus.OPEN);
        return (Long) query.getSingleResult();
    }

    @Override
    public ExchangeRate getExchangeRate(String date) {
        return em.find(ExchangeRate.class, date);
    }

    @Override
    public void createOrUpdateExchangeRate(ExchangeRate exchangeRate) {
        em.merge(exchangeRate);
    }
}