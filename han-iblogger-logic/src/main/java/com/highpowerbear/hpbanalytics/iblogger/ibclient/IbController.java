package com.highpowerbear.hpbanalytics.iblogger.ibclient;

import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerData;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbloggerUtil;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import com.highpowerbear.hpbanalytics.iblogger.persistence.IbloggerDao;
import com.ib.client.EClientSocket;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 *
 * @author Robert
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(IbloggerDefinitions.LOGGER);

    @Inject private IbloggerDao ibloggerDao;
    @Inject private IbloggerData ibloggerData;

    public void connect(IbAccount ibAccount) {
        IbConnection c = ibloggerData.getIbConnectionMap().get(ibAccount);

        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl(ibAccount)));
        }
        if (c.getClientSocket() != null && !c.getClientSocket().isConnected()) {
            c.setAccounts(null);
            c.setIsConnected(false);
            l.info("Connecting ibAccount " + ibAccount.print());
            c.getClientSocket().eConnect(ibAccount.getHost(), ibAccount.getPort(), IbloggerDefinitions.IB_CONNECT_CLIENT_ID);
            IbloggerUtil.waitMilliseconds(IbloggerDefinitions.ONE_SECOND);
            if (isConnected(ibAccount)) {
                c.setIsConnected(true);
                l.info("Sucessfully connected ibAccount " + ibAccount.print());
                requestOpenOrders(ibAccount);
                requestAccounts(ibAccount);
            }
        }
    }

    public void disconnect(IbAccount ibAccount) {
        IbConnection c = ibloggerData.getIbConnectionMap().get(ibAccount);
        if (c.getClientSocket() != null && c.getClientSocket().isConnected()) {
            l.info("Disconnecting ibAccount " + ibAccount.print());
            c.getClientSocket().eDisconnect();
            IbloggerUtil.waitMilliseconds(IbloggerDefinitions.ONE_SECOND);
            if (!isConnected(ibAccount)) {
                l.info("Successfully disconnected ibAccount " + ibAccount.print());
                c.clear();
            }
        }
    }

    public boolean isConnected(IbAccount ibAccount) {
        IbConnection c = ibloggerData.getIbConnectionMap().get(ibAccount);
        return (c.getClientSocket() != null && c.getClientSocket().isConnected());
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = ibloggerData.getIbConnectionMap().get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }

    private void requestAccounts(IbAccount ibAccount) {
        l.info("Requesting account for ibAccount " + ibAccount.print());
        ibloggerData.getIbConnectionMap().get(ibAccount).getClientSocket().reqManagedAccts();
    }
}
