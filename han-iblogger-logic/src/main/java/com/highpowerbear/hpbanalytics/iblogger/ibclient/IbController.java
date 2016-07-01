package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    @Inject private IbLoggerDao ibLoggerDao;
    @Inject private Instance<IbListener> ibListeners;

    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    public IbConnection getIbConnection(IbAccount ibAccount) {
        return ibConnectionMap.get(ibAccount);
    }

    @PostConstruct
    private void init() {
        ibLoggerDao.getIbAccounts().forEach(ibAccount -> {
            EClientSocket eClientSocket = new EClientSocket(ibListeners.get().configure(ibAccount));
            IbConnection ibConnection = new IbConnection(IbLoggerDefinitions.IbConnectionType.IBLOGGER, ibAccount.getHost(), ibAccount.getPort(), IbLoggerDefinitions.IB_CONNECT_CLIENT_ID, eClientSocket);
            ibConnectionMap.put(ibAccount, ibConnection);
        });
    }

    @PreDestroy
    private void finish() {
        ibConnectionMap.keySet().forEach(this::disconnect);
    }

    public void connect(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).connect();
    }

    public void disconnect(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).disconnect();
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = ibConnectionMap.get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }
}
