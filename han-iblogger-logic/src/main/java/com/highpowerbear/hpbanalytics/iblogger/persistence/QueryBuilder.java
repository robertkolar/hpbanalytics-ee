package com.highpowerbear.hpbanalytics.iblogger.persistence;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.rest.model.IbOrderFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.10.2015.
 */
@ApplicationScoped
public class QueryBuilder {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    public Query buildFilteredIbOrdersQuery(EntityManager em, IbAccount ibAccount, IbOrderFilter filter, boolean isCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(isCount ? "COUNT(io)" : "io").append(" FROM IbOrder io WHERE io.ibAccount = :ibAccount");

        for (IbLoggerDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            sb.append(" AND io.symbol ").append(op.getSql()).append(" :").append(op.name()).append("_").append(IbLoggerDefinitions.IbOrderFilterField.SYMBOL.getVarName());
        }
        for (IbLoggerDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            sb.append(" AND io.secType ").append(op.getSql()).append(" :").append(op.name()).append("_").append(IbLoggerDefinitions.IbOrderFilterField.SEC_TYPE.getVarName());
        }
        for (IbLoggerDefinitions.FilterOperatorCalendar op : filter.getSubmitDateFilterMap().keySet()) {
            String varName = IbLoggerDefinitions.IbOrderFilterField.SUBMIT_DATE.getVarName();
            if (IbLoggerDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                sb.append(" AND io.submitDate > :from_").append(varName).append(" AND io.submitDate < :to_").append(varName);
            } else {
                sb.append(" AND io.submitDate ").append(op.getSql()).append(" :").append(op.name()).append("_").append(varName);
            }
        }
        for (IbLoggerDefinitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            sb.append(" AND io.status ").append(op.getSql()).append(" :").append(op.name()).append("_").append(IbLoggerDefinitions.IbOrderFilterField.STATUS.getVarName());
        }

        sb.append(isCount ? "" : " ORDER BY io.submitDate DESC");
        Query q = (isCount ? em.createQuery(sb.toString()) : em.createQuery(sb.toString(), IbOrder.class));

        q.setParameter("ibAccount", ibAccount);

        for (IbLoggerDefinitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            boolean isLike = IbLoggerDefinitions.FilterOperatorString.LIKE.equals(op);
            q.setParameter(op.name() + "_" + IbLoggerDefinitions.IbOrderFilterField.SYMBOL.getVarName(), (isLike ? "%" : "") + filter.getSymbolFilterMap().get(op) + (isLike ? "%" : ""));
        }
        for (IbLoggerDefinitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + IbLoggerDefinitions.IbOrderFilterField.SEC_TYPE.getVarName(), filter.getSecTypeFilterMap().get(op));
        }
        for (IbLoggerDefinitions.FilterOperatorCalendar op : filter.getSubmitDateFilterMap().keySet()) {
            String varName = IbLoggerDefinitions.IbOrderFilterField.SUBMIT_DATE.getVarName();
            if (IbLoggerDefinitions.FilterOperatorCalendar.EQ.equals(op)) {
                Calendar from = filter.getSubmitDateFilterMap().get(op);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(from.getTimeInMillis());
                to.add(Calendar.DATE, 1);
                q.setParameter("from_" + varName, from);
                q.setParameter("to_" + varName, to);
            } else {
                q.setParameter(op.name() + "_" + varName, filter.getSubmitDateFilterMap().get(op));
            }
        }
        for (IbLoggerDefinitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + IbLoggerDefinitions.IbOrderFilterField.STATUS.getVarName(), filter.getStatusFilterMap().get(op));
        }

        //l.info("Generated query=" + sb.toString());
        return q;
    }
}
