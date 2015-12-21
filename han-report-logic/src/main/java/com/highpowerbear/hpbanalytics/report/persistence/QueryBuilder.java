package com.highpowerbear.hpbanalytics.report.persistence;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.rest.model.ExecutionFilter;
import com.highpowerbear.hpbanalytics.report.rest.model.TradeFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 21.10.2015.
 */
@Named
@ApplicationScoped
public class QueryBuilder {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    public Query buildFilteredExecutionsQuery(EntityManager em, Report report, ExecutionFilter filter, boolean isCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(isCount ? "COUNT(e)" : "e").append(" FROM Execution e WHERE e.report = :report");

        for (ReportDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            sb.append(" AND e.symbol ").append(op.getSql()).append(" :").append(op.name()).append("_").append(ReportDefinitions.ExecutionFilterField.SYMBOL.getVarName());
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            sb.append(" AND e.secType ").append(op.getSql()).append(" :").append(op.name()).append("_").append(ReportDefinitions.ExecutionFilterField.SEC_TYPE.getVarName());
        }
        for (ReportDefinitions.FilterOperatorCalendar op : filter.getFillDateFilterMap().keySet()) {
            String varName = ReportDefinitions.ExecutionFilterField.FILL_DATE.getVarName();
            if (ReportDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                sb.append(" AND e.fillDate > :from_").append(varName).append(" AND e.fillDate < :to_").append(varName);
            } else {
                sb.append(" AND e.fillDate ").append(op.getSql()).append(" :").append(op.name()).append("_").append(varName);
            }
        }

        sb.append(isCount ? "" : " ORDER BY e.fillDate DESC");
        Query q = (isCount ? em.createQuery(sb.toString()) : em.createQuery(sb.toString(), Execution.class));

        q.setParameter("report", report);

        for (ReportDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            boolean isLike = ReportDefinitions.FilterOperatorString.LIKE.equals(op);
            q.setParameter(op.name() + "_" + ReportDefinitions.ExecutionFilterField.SYMBOL.getVarName(), (isLike ? "%" : "") + filter.getSymbolFilterMap().get(op) + (isLike ? "%" : ""));
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + ReportDefinitions.ExecutionFilterField.SEC_TYPE.getVarName(), filter.getSecTypeFilterMap().get(op));
        }
        for (ReportDefinitions.FilterOperatorCalendar op : filter.getFillDateFilterMap().keySet()) {
            String varName = ReportDefinitions.ExecutionFilterField.FILL_DATE.getVarName();
            if (ReportDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                Calendar from = filter.getFillDateFilterMap().get(op);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(from.getTimeInMillis());
                to.add(Calendar.DATE, 1);
                q.setParameter("from_" + varName, from);
                q.setParameter("to_" + varName, to);
            } else {
                q.setParameter(op.name() + "_" + varName, filter.getFillDateFilterMap().get(op));
            }
        }

        //l.info("Generated query=" + sb.toString());
        return q;
    }

    public Query buildFilteredTradesQuery(EntityManager em, Report report, TradeFilter filter, boolean isCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(isCount ? "COUNT(t)" : "t").append(" FROM Trade t WHERE t.report = :report");

        for (ReportDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            sb.append(" AND t.symbol ").append(op.getSql()).append(" :").append(op.name()).append("_").append(ReportDefinitions.TradeFilterField.SYMBOL.getVarName());
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            sb.append(" AND t.secType ").append(op.getSql()).append(" :").append(op.name()).append("_").append(ReportDefinitions.TradeFilterField.SEC_TYPE.getVarName());
        }
        for (ReportDefinitions.FilterOperatorCalendar op : filter.getOpenDateFilterMap().keySet()) {
            String varName = ReportDefinitions.TradeFilterField.OPEN_DATE.getVarName();
            if (ReportDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                sb.append(" AND t.openDate > :from_").append(varName).append(" AND t.openDate < :to_").append(varName);
            } else {
                sb.append(" AND t.openDate ").append(op.getSql()).append(" :").append(op.name()).append("_").append(varName);
            }
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            sb.append(" AND t.status ").append(op.getSql()).append(" :").append(op.name()).append("_").append(ReportDefinitions.TradeFilterField.STATUS.getVarName());
        }

        sb.append(isCount ? "" : " ORDER BY t.openDate DESC");
        Query q = (isCount ? em.createQuery(sb.toString()) : em.createQuery(sb.toString(), Trade.class));

        q.setParameter("report", report);

        for (ReportDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            boolean isLike = ReportDefinitions.FilterOperatorString.LIKE.equals(op);
            q.setParameter(op.name() + "_" + ReportDefinitions.TradeFilterField.SYMBOL.getVarName(), (isLike ? "%" : "") + filter.getSymbolFilterMap().get(op) + (isLike ? "%" : ""));
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + ReportDefinitions.TradeFilterField.SEC_TYPE.getVarName(), filter.getSecTypeFilterMap().get(op));
        }
        for (ReportDefinitions.FilterOperatorCalendar op : filter.getOpenDateFilterMap().keySet()) {
            String varName = ReportDefinitions.TradeFilterField.OPEN_DATE.getVarName();
            if (ReportDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                Calendar from = filter.getOpenDateFilterMap().get(op);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(from.getTimeInMillis());
                to.add(Calendar.DATE, 1);
                q.setParameter("from_" + varName, from);
                q.setParameter("to_" + varName, to);
            } else {
                q.setParameter(op.name() + "_" + varName, filter.getOpenDateFilterMap().get(op));
            }
        }
        for (ReportDefinitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + ReportDefinitions.TradeFilterField.STATUS.getVarName(), filter.getStatusFilterMap().get(op));
        }

        //l.info("Generated query=" + sb.toString());
        return q;
    }
}
