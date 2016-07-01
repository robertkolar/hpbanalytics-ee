package com.highpowerbear.hpbanalytics.iblogger.model;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerUtil;
import com.ib.client.EClientSocket;

import javax.xml.bind.annotation.*;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/4/15.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class IbConnection {
    private static final Logger l = Logger.getLogger(IbLoggerDefinitions.LOGGER);

    private IbLoggerDefinitions.IbConnectionType type;
    private String host;
    private Integer port;
    private Integer clientId;
    private String accounts; // csv, filled upon connection to IB, main account + FA subaccounts if any
    private boolean markConnected = false;
    @XmlTransient
    private EClientSocket eClientSocket; // null means not connected yet or manually disconnected

    public IbConnection() {
    }

    public IbConnection(IbLoggerDefinitions.IbConnectionType type, String host, Integer port, Integer clientId, EClientSocket eClientSocket) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.eClientSocket = eClientSocket;
    }

    private String print() {
        return "mkt data, host=" + host + ", port=" + port + ", clientId=" + clientId;
    }

    public void connect() {
        if (eClientSocket == null) {
            return;
        }
        this.markConnected = true;
        if (!isConnected()) {
            l.info("Connecting " + print());
            eClientSocket.eConnect(host, port, clientId);
            IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND_MILLIS);
            if (isConnected()) {
                l.info("Sucessfully connected " + print());
            }
        }
    }

    public void disconnect() {
        if (eClientSocket == null) {
            return;
        }
        this.markConnected = false;
        if (isConnected()) {
            l.info("Disconnecting " + print());
            eClientSocket.eDisconnect();
            IbLoggerUtil.waitMilliseconds(IbLoggerDefinitions.ONE_SECOND_MILLIS);
            if (!isConnected()) {
                l.info("Successfully disconnected " + print());
                this.accounts = null;
            }
        }
    }
    @XmlElement
    public Boolean isConnected() {
        return eClientSocket != null && eClientSocket.isConnected();
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getAccounts() {
        return accounts;
    }

    public IbLoggerDefinitions.IbConnectionType getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getClientId() {
        return clientId;
    }

    public boolean isMarkConnected() {
        return markConnected;
    }

    public EClientSocket getClientSocket() {
        return eClientSocket;
    }
}
