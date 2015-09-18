package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Robert
 */
@Stateless
public class ReportDaoImpl implements Serializable, ReportDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Report getReportByOrigin(String origin) {
        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r WHERE r.origin = :origin", Report.class);
        q.setParameter("origin", origin);
        List<Report> list = q.getResultList();
        return (!list.isEmpty() ? list.get(0) : null);
    }

    @Override
    public Report findReport(Integer id) {
        return em.find(Report.class, id);
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
        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r", Report.class);
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
    public List<Execution> getExecutions(Report report, Integer start, Integer limit) {
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report = :report ORDER BY e.fillDate DESC", Execution.class);
        q.setParameter("report", report);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
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
        em.remove(execution);
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
    public List<Trade> getTrades(Report report, Integer start, Integer limit) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report ORDER BY t.dateOpened DESC", Trade.class);
        q.setParameter("report", report);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        List<Trade> list = q.getResultList();
        q.getResultList().forEach(com.highpowerbear.hpbanalytics.report.entity.Trade::getSplitExecutions);
        return list;
    }

    @Override
    public List<Trade> getTrades(Report report, String underlying) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report" +  (underlying != null ? " AND t.underlying = :underlying" : "") + " ORDER BY t.dateOpened ASC", Trade.class);
        q.setParameter("report", report);
        if (underlying != null && !underlying.equals(ReportDefinitions.ALL_UNDERLYINGS)) {
            q.setParameter("underlying", underlying);
        }
        List<Trade> list = q.getResultList();
        list.forEach(com.highpowerbear.hpbanalytics.report.entity.Trade::getSplitExecutions);
        return list;
    }

    @Override
    public List<Trade> getTradesAffectedByExecution(Execution e) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report AND (t.dateClosed >= :eDate OR t.status = :tradeStatus) AND t.symbol = :eSymbol ORDER BY t.dateOpened ASC", Trade.class);
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
        for (Trade trade : this.getTrades(report, null)) {
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
            trade = em.find(Trade.class, trade.getId()); // make sure it is managed by entitymanager
            // it is managed, since trade is managed
            trade.getSplitExecutions().forEach(em::remove);
            em.remove(trade);
        }
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
}