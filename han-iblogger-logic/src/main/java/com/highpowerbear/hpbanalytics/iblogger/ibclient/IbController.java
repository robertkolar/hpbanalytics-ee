package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbLoggerDao;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
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

    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    @PostConstruct
    private void init() {
        for (IbAccount ibAccount : ibLoggerDao.getIbAccounts()) {
            ibConnectionMap.put(ibAccount, new IbConnection());
        }
    }

    @PreDestroy
    private void finish() {
        ibConnectionMap.keySet().forEach(this::disconnect);
    }

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    public void connect(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);

        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl(ibAccount)));
        }
        if (c.getClientSocket() != null && !c.getClientSocket().isConnected()) {
            c.setAccounts(null);
            c.setIsConnected(false);
            l.info("Connecting ibAccount " + ibAccount.print());
            c.getClientSocket().eConnect(ibAccount.getHost(), ibAccount.getPort(), IbLoggerDefinitions.IB_CONNECT_CLIENT_ID);
            IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND);
            if (isConnected(ibAccount)) {
                c.setIsConnected(true);
                l.info("Sucessfully connected ibAccount " + ibAccount.print());
                requestOpenOrders(ibAccount);
                requestAccounts(ibAccount);
            }
        }
    }

    public void disconnect(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        if (c.getClientSocket() != null && c.getClientSocket().isConnected()) {
            l.info("Disconnecting ibAccount " + ibAccount.print());
            c.getClientSocket().eDisconnect();
            IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND);
            if (!isConnected(ibAccount)) {
                l.info("Successfully disconnected ibAccount " + ibAccount.print());
                c.clear();
            }
        }
    }

    public boolean isConnected(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        return (c.getClientSocket() != null && c.getClientSocket().isConnected());
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = ibConnectionMap.get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }

    private void requestAccounts(IbAccount ibAccount) {
        l.info("Requesting account for ibAccount " + ibAccount.print());
        ibConnectionMap.get(ibAccount).getClientSocket().reqManagedAccts();
    }
}
