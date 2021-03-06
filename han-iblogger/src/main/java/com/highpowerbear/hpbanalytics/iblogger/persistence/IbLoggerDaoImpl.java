package com.highpowerbear.hpbanalytics.iblogger.persistence;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.rest.model.IbOrderFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by robertk on 3/28/15.
 */
@Stateless
public class IbLoggerDaoImpl implements IbLoggerDao {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @PersistenceContext(unitName = "hpbanalytics-PU")
    private EntityManager em;
    @Inject private QueryBuilder queryBuilder;

    private final String B = "BEGIN " + this.getClass().getSimpleName() + ".";
    private final String E = "END " + this.getClass().getSimpleName() + ".";

    @Override
    public IbAccount findIbAccount(String accountId) {
        return em.find(IbAccount.class, accountId);
    }

    @Override
    public List<IbAccount> getIbAccounts() {
        TypedQuery<IbAccount> q = em.createQuery("SELECT ia FROM IbAccount ia ORDER BY ia.port", IbAccount.class);
        return q.getResultList();
    }

    @Override
    public IbAccount updateIbAccount(IbAccount ibAccount) {
        return em.merge(ibAccount);
    }

    @Override
    public List<IbOrder> getFilteredIbOrders(IbAccount ibAccount, IbOrderFilter filter, Integer start, Integer limit) {
        Query q = queryBuilder.buildFilteredIbOrdersQuery(em, ibAccount, filter, false);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumFilteredIbOrders(IbAccount ibAccount, IbOrderFilter filter) {
        Query q = queryBuilder.buildFilteredIbOrdersQuery(em, ibAccount, filter, true);
        //Query q = em.createQuery("SELECT COUNT(io) FROM IbOrder io WHERE io.ibAccount = :ibAccount");
        return (Long) q.getSingleResult();
    }

    @Override
    public List<IbOrder> getOpenIbOrders(IbAccount ibAccount) {
        TypedQuery<IbOrder> q = em.createQuery("SELECT io FROM IbOrder io WHERE io.ibAccount = :ibAccount AND io.status IN :statuses", IbOrder.class);
        q.setParameter("ibAccount", ibAccount);
        Set<IbLoggerDefinitions.IbOrderStatus> statuses = new HashSet<>();
        statuses.add(IbLoggerDefinitions.IbOrderStatus.SUBMITTED);
        statuses.add(IbLoggerDefinitions.IbOrderStatus.UPDATED);
        q.setParameter("statuses", statuses);
        return q.getResultList();
    }

    @Override
    public void newIbOrder(IbOrder ibOrder) {
        l.info(B + "newIbOrder, dbId=" + ibOrder.getId() + ", account=" + ibOrder.getIbAccount().getAccountId() + ", permId=" + ibOrder.getPermId());
        em.persist(ibOrder);
        l.info(E + "newIbOrder, dbId=" + ibOrder.getId() + ", account=" + ibOrder.getIbAccount().getAccountId() + ", permId=" + ibOrder.getPermId());
    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {
        l.info(B + "updateIbOrder, account=" + ibOrder.getIbAccount().getAccountId() + ", permId=" + ibOrder.getPermId());
        em.merge(ibOrder);
        l.info(E + "updateIbOrder, account=" + ibOrder.getIbAccount().getAccountId() + ", permId=" + ibOrder.getPermId());
    }

    @Override
    public IbOrder getIbOrderByPermId(IbAccount ibAccount, Integer permId) {
        TypedQuery<IbOrder> q = em.createQuery("SELECT io FROM IbOrder io WHERE io.ibAccount = :ibAccount AND io.permId = :permId", IbOrder.class);
        q.setParameter("ibAccount", ibAccount);
        q.setParameter("permId", permId);
        List<IbOrder> ibOrders = q.getResultList();
        return (!ibOrders.isEmpty() ? ibOrders.get(0) : null);
    }
}
