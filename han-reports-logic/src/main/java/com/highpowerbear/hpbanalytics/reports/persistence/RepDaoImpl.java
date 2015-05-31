package com.highpowerbear.hpbanalytics.reports.persistence;

import com.highpowerbear.hpbanalytics.reports.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.reports.entity.Execution;
import com.highpowerbear.hpbanalytics.reports.entity.Report;
import com.highpowerbear.hpbanalytics.reports.entity.SplitExecution;
import com.highpowerbear.hpbanalytics.reports.entity.Trade;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@Stateless
public class RepDaoImpl implements Serializable, RepDao {
    private static final Logger l = Logger.getLogger(RepDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

    @Override
    public Date getFirstExecutionDate(Report report) {
        l.info("START getFirstExecutionDate for " +  report.getName());
        Query q = em.createQuery("SELECT MIN(e.fillDate) FROM Execution e WHERE e.report = :report");
        q.setParameter("report", report);
        Calendar cal = (Calendar) q.getResultList().get(0);
        Date firstDate = (cal != null ? new Date(cal.getTimeInMillis()) : null);
        String firstDateDisplay = (cal != null ? df.format(cal.getTime()) : null);
        l.info("END getFirstExecutionDate for " +  report.getName() + ", date=" + firstDateDisplay);
        return firstDate;
    }

    @Override
    public String getLastExecutionDate(Report report) {
        l.info("START getLastExecutionDate for " +  report.getName());
        Query q = em.createQuery("SELECT MAX(e.fillDate) FROM Execution e WHERE e.report = :report");
        q.setParameter("report", report);
        Calendar cal = (Calendar) q.getResultList().get(0);
        String lastDate = (cal != null ? String.valueOf(cal.getTimeInMillis()) : null);
        String lastDateDisplay = (cal != null ? df.format(cal.getTime()) : null);
        l.info("END getLastExecutionDate for " +  report.getName() + ", date=" + lastDateDisplay);
        return lastDate;
        
    }

    @Override
    public Report getReportByOrigin(String origin) {
        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r WHERE r.origin = :origin", Report.class);
        q.setParameter("origin", origin);
        List<Report> list = q.getResultList();
        return (!list.isEmpty() ? list.get(0) : null);
    }

    @Override
    public void deleteReport(Report report) {
        l.info("START deleteReport " +  report.getName());
        Report reportDb = em.find(Report.class, report.getId());
        this.deleteAllTrades(report);
        this.getExecutions(report).forEach(em::remove);
        em.remove(reportDb);
        l.info("END deleteReport " +  report.getName());
    }

    @Override
    public List<Report> getReports() {
        TypedQuery<Report> q = em.createQuery("SELECT r FROM Report r", Report.class);
        return q.getResultList();
    }

    @Override
    public Long getNumTrades(Report report) {
        l.info("START getNumTrades for " + report.getName());
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.report = :report");
        query.setParameter("report", report);
        Long nt = (Long) query.getSingleResult();
        l.info("END getNumTrades for " + report.getName() + ", count=" + nt);
        return nt;
    }

    @Override
    public Long getNumOpenTrades(Report report) {
        l.info("START getNumOpenTrades for " + report.getName());
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.report = :report AND t.status = :tradeStatus");
        query.setParameter("report", report);
        query.setParameter("tradeStatus", RepDefinitions.TradeStatus.OPEN);
        Long nt = (Long) query.getSingleResult();
        l.info("END getNumOpenTrades for " + report.getName() + ", count=" + nt);
        return nt;
    }

    @Override
    public List<Trade> getTrades(Report report, boolean descending) {
        l.info("START getTrades for " + report.getName());
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report ORDER BY t.dateOpened " + (descending ? "DESC" : "ASC"), Trade.class);
        q.setParameter("report", report);
        List<Trade> list = q.getResultList();
        q.getResultList().forEach(com.highpowerbear.hpbanalytics.reports.entity.Trade::getSplitExecutions);
        l.info("END getTrades for " + report.getName() + ", count=" + list.size());
        return list;
    }

    @Override
    public List<Trade> getTrades(Report report, String underlying) {
        if (underlying == null) {
            return getTrades(report, false);
        }
        l.info("START getTrades for " + report.getName() + ", undl=" + underlying);
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report AND t.underlying = :underlying ORDER BY t.dateOpened ASC", Trade.class);
        q.setParameter("report", report);
        q.setParameter("underlying", underlying);
        List<Trade> list = q.getResultList();
        list.forEach(com.highpowerbear.hpbanalytics.reports.entity.Trade::getSplitExecutions);
        l.info("END getTrades for " + report.getName() + ", undl=" + underlying + ", count=" + list.size());
        return list;
    }

    @Override
    public List<Trade> getTradesAffectedByExecution(Execution e) {
        l.info("START getTradesAffectedByExecution " +  e.print());
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.report = :report AND (t.dateClosed >= :eDate OR t.status = :tradeStatus) AND t.symbol = :eSymbol ORDER BY t.dateOpened ASC", Trade.class);
        q.setParameter("tradeStatus", RepDefinitions.TradeStatus.OPEN);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        List<Trade> tl = q.getResultList();
        tl.forEach(com.highpowerbear.hpbanalytics.reports.entity.Trade::getSplitExecutions);
        l.info("END getTradesAffectedByExecution, count=" + tl.size());
        return tl;
    }

    @Override
    public void addTrades(List<Trade> trades) {
        String esName = trades.iterator().next().getReport().getName();
        l.info("START addTrades for " +  esName);
        StringBuilder sb = new StringBuilder();
        for (Trade t : trades) {
            sb.append("Trade: ").append(t.print()).append("\n");
            for (SplitExecution se : t.getSplitExecutions()) {
                sb.append(se.print()).append("\n");
            }
            em.persist(t);
        }
        l.info(sb.toString());
        l.info("END addTrades for " +  esName + ", count=" + trades.size());
    }

    @Override
    public void deleteAllTrades(Report report) {
        l.info("START deleteTrades for " +  report.getName());
        int count = 0;
        for (Trade trade : this.getTrades(report, false)) {
            for (SplitExecution se : trade.getSplitExecutions()) {
                em.remove(se); // it is managed, since trade is managed
                l.info("Deleted splitExecution: " + se.print());
            }
            em.remove(trade);
            count++;
        }
        l.info("END deleteTrades for " +  report.getName() + ", count=" + count);
    }

    @Override
    public void deleteTrades(List<Trade> trades) {
        if (trades == null) {
            return;
        }
        l.info("START deleteTrades, count=" + trades.size());
        for (Trade trade : trades) {
            trade = em.find(Trade.class, trade.getId()); // make sure it is managed by entitymanager
            for (SplitExecution se : trade.getSplitExecutions()) {
                em.remove(se); // it is managed, since trade is managed
                l.info("Deleted splitExecution: " + se.print());
            }
            em.remove(trade);
            l.info("Deleted trade: " + trade.print());
        }
        l.info("END deleteTrades");
    }

    @Override
    public Long getNumExecutions(Report report) {
        l.info("START getNumExecutions for " + report.getName());
        Query query = em.createQuery("SELECT COUNT(e) FROM Execution e WHERE e.report = :report");
        query.setParameter("report", report);
        Long ne = (Long) query.getSingleResult();
        l.info("END getNumExecutions for " + report.getName() + ", count=" + ne);
        return ne;
    }

    @Override
    public List<Execution> getExecutions(Report report, boolean descending) {
        l.info("START getExecutions for " + report.getName());
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report = :report ORDER BY e.fillDate " + (descending ? "DESC" : "ASC"), Execution.class);
        q.setParameter("report", report);
        List<Execution> res = q.getResultList();
        l.info("END getExecutions for " + report.getName() + ", count=" + res.size());
        return res;
    }

    @Override
    public List<Execution> getExecutions(Report report) {
        return getExecutions(report, false);
    }

    @Override
    public List<Execution> getRecentExecutions(Integer limit) {
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e ORDER BY e.fillDate DESC", Execution.class);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<Execution> getExecutionsAfterExecution(Execution e) {
        l.info("START getExecutionsAfterExecution, e=" + e.print());
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report = :report AND e.fillDate > :eDate AND e.symbol = :eSymbol ORDER BY e.fillDate ASC", Execution.class);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        List<Execution> el = q.getResultList();
        l.info("END getExecutionsAfterExecution, count=" + (el != null ? el.size() : 0));
        return el;
    }

    @Override
    public List<Execution> getExecutionsAfterExecutionInclusive(Execution e) {
        l.info("START getExecutionsAfterExecutionInclusive, e=" + e.print());
        TypedQuery<Execution> q = em.createQuery("SELECT e FROM Execution e WHERE e.report= :report AND e.fillDate >= :eDate AND e.symbol = :eSymbol ORDER BY e.fillDate ASC", Execution.class);
        q.setParameter("report", e.getReport());
        q.setParameter("eDate", e.getFillDate());
        q.setParameter("eSymbol", e.getSymbol());
        List<Execution> el = q.getResultList();
        l.info("END getExecutionsAfterExecutionInclusive, count=" + (el != null ? el.size() : 0));
        return el;
    }

    @Override
    public boolean existsExecution(Execution e) {
        Execution eDb = em.find(Execution.class, e.getId());
        return (eDb != null);
    }

    @Override
    public void newExecution(Execution execution) {
        l.info("START newExecution " + execution.print());
        em.persist(execution);
        l.info("END newExecution");
    }

    @Override
    public void deleteExecutions(List<Execution> executions) {
        if (executions == null) {
            return;
        }
        l.info("START deleteExecutions, count=" + executions.size());
        for (Execution execution : executions) {
            execution = em.find(Execution.class, execution.getId());
            em.remove(execution);
            l.info("Deleted execution: " + execution.print());
        }
        l.info("END deleteExecutions, count=" + executions.size());
    }

    @Override
    public List<String> getUnderlyings(Report report) {
        l.info("START getUnderlyings for " + report.getName());
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT e.underlying FROM Execution e WHERE e.report = :report", String.class);
        query.setParameter("report", report);
        List<String> undls = query.getResultList();
        l.info("END getUnderlyings for " + report.getName() + ", count=" + undls.size());
        return undls;
    }

    @Override
    public Long getNumUnderlyings(Report report) {
        l.info("START getNumUnderlyings for " + report.getName());
        Query query = em.createQuery("SELECT COUNT(DISTINCT e.underlying) FROM Execution e WHERE e.report = :report");
        query.setParameter("report", report);
        Long ne = (Long) query.getSingleResult();
        l.info("END getNumUnderlyings for " + report.getName() + ", count=" + ne);
        return ne;
    }

    @Override
    public Long getNumOpenUnderlyings(Report report) {
        l.info("START getNumOpenUnderlyings for " + report.getName());
        Query query = em.createQuery("SELECT COUNT(DISTINCT se.execution.underlying) FROM SplitExecution se WHERE se.execution.report = :report AND se.trade.status = :tradeStatus");
        query.setParameter("report", report);
        query.setParameter("tradeStatus", RepDefinitions.TradeStatus.OPEN);
        Long ne = (Long) query.getSingleResult();
        l.info("END getNumOpenUnderlyings for " + report.getName() + ", count=" + ne);
        return ne;
    }
}