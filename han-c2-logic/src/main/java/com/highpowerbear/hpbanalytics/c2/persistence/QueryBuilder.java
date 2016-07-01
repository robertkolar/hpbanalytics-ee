package com.highpowerbear.hpbanalytics.c2.persistence;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.rest.model.C2SignalFilter;
import com.highpowerbear.hpbanalytics.c2.rest.model.InputRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.10.2015.
 */
@ApplicationScoped
public class QueryBuilder {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    public Query buildFilteredInputRequestsQuery(EntityManager em, InputRequestFilter filter, boolean isCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(isCount ? "COUNT(ir)" : "ir").append(" FROM InputRequest ir WHERE ir.id IS NOT NULL");

        for (C2Definitions.FilterOperatorCalendar op : filter.getReceivedDateFilterMap().keySet()) {
            String varName = C2Definitions.InputRequestFilterField.RECEIVED_DATE.getVarName();
            if (C2Definitions.FilterOperatorCalendar.EQ.equals(op)) {
                sb.append(" AND ir.receivedDate > :from_").append(varName).append(" AND ir.receivedDate < :to_").append(varName);
            } else {
                sb.append(" AND ir.receivedDate ").append(op.getSql()).append(" :").append(op.name()).append("_").append(varName);
            }
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            sb.append(" AND ir.status ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.InputRequestFilterField.STATUS.getVarName());
        }
        for (C2Definitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            sb.append(" AND ir.symbol ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.InputRequestFilterField.SYMBOL.getVarName());
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            sb.append(" AND ir.secType ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.InputRequestFilterField.SEC_TYPE.getVarName());
        }

        sb.append(isCount ? "" : " ORDER BY ir.receivedDate DESC");
        Query q = (isCount ? em.createQuery(sb.toString()) : em.createQuery(sb.toString(), InputRequest.class));

        for (C2Definitions.FilterOperatorCalendar op : filter.getReceivedDateFilterMap().keySet()) {
            String varName = C2Definitions.InputRequestFilterField.RECEIVED_DATE.getVarName();
            if (C2Definitions.FilterOperatorCalendar.EQ.equals(op)) {
                Calendar from = filter.getReceivedDateFilterMap().get(op);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(from.getTimeInMillis());
                to.add(Calendar.DATE, 1);
                q.setParameter("from_" + varName, from);
                q.setParameter("to_" + varName, to);
            } else {
                q.setParameter(op.name() + "_" + varName, filter.getReceivedDateFilterMap().get(op));
            }
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getStatusFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + C2Definitions.InputRequestFilterField.STATUS.getVarName(), filter.getStatusFilterMap().get(op));
        }
        for (C2Definitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            boolean isLike = C2Definitions.FilterOperatorString.LIKE.equals(op);
            q.setParameter(op.name() + "_" + C2Definitions.InputRequestFilterField.SYMBOL.getVarName(), (isLike ? "%" : "") + filter.getSymbolFilterMap().get(op) + (isLike ? "%" : ""));
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getSecTypeFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + C2Definitions.InputRequestFilterField.SEC_TYPE.getVarName(), filter.getSecTypeFilterMap().get(op));
        }

        //l.info("Generated query=" + sb.toString());
        return q;
    }

    public Query buildFilteredC2SignalsQuery(EntityManager em, C2System c2System, C2SignalFilter filter, boolean isCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(isCount ? "COUNT(cs)" : "cs").append(" FROM C2Signal cs WHERE cs.c2System = :c2System");

        for (C2Definitions.FilterOperatorCalendar op : filter.getCreatedDateFilterMap().keySet()) {
            String varName = C2Definitions.C2SignalFilterField.CREATED_DATE.getVarName();
            if (C2Definitions.FilterOperatorCalendar.EQ.equals(op)) {
                sb.append(" AND cs.createdDate > :from_").append(varName).append(" AND cs.createdDate < :to_").append(varName);
            } else {
                sb.append(" AND cs.createdDate ").append(op.getSql()).append(" :").append(op.name()).append("_").append(varName);
            }
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getPublishStatusFilterMap().keySet()) {
            sb.append(" AND cs.publishStatus ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.C2SignalFilterField.PUBLISH_STATUS.getVarName());
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getPollStatusFilterMap().keySet()) {
            sb.append(" AND cs.pollStatus ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.C2SignalFilterField.POLL_STATUS.getVarName());
        }
        for (C2Definitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            sb.append(" AND cs.symbol ").append(op.getSql()).append(" :").append(op.name()).append("_").append(C2Definitions.C2SignalFilterField.SYMBOL.getVarName());
        }

        sb.append(isCount ? "" : " ORDER BY cs.createdDate DESC");
        Query q = (isCount ? em.createQuery(sb.toString()) : em.createQuery(sb.toString(), C2Signal.class));

        q.setParameter("c2System", c2System);

        for (C2Definitions.FilterOperatorCalendar op : filter.getCreatedDateFilterMap().keySet()) {
            String varName = C2Definitions.C2SignalFilterField.CREATED_DATE.getVarName();
            if (C2Definitions.FilterOperatorCalendar.EQ.equals(op)) {
                Calendar from = filter.getCreatedDateFilterMap().get(op);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(from.getTimeInMillis());
                to.add(Calendar.DATE, 1);
                q.setParameter("from_" + varName, from);
                q.setParameter("to_" + varName, to);
            } else {
                q.setParameter(op.name() + "_" + varName, filter.getCreatedDateFilterMap().get(op));
            }
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getPublishStatusFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + C2Definitions.C2SignalFilterField.PUBLISH_STATUS.getVarName(), filter.getPublishStatusFilterMap().get(op));
        }
        for (C2Definitions.FilterOperatorEnum op : filter.getPollStatusFilterMap().keySet()) {
            q.setParameter(op.name() + "_" + C2Definitions.C2SignalFilterField.POLL_STATUS.getVarName(), filter.getPollStatusFilterMap().get(op));
        }
        for (C2Definitions.FilterOperatorString op : filter.getSymbolFilterMap().keySet()) {
            boolean isLike = C2Definitions.FilterOperatorString.LIKE.equals(op);
            q.setParameter(op.name() + "_" + C2Definitions.C2SignalFilterField.SYMBOL.getVarName(), (isLike ? "%" : "") + filter.getSymbolFilterMap().get(op) + (isLike ? "%" : ""));
        }

        //l.info("Generated query=" + sb.toString());
        return q;
    }
}