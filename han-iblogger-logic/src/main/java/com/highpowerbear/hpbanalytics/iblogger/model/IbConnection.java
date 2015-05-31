package com.highpowerbear.hpbanalytics.iblogger.model;

import com.ib.client.EClientSocket;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by robertk on 4/4/15.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class IbConnection {
    private String accounts; // csv, filled upon connection to IB, main account + FA subaccounts if any
    private Boolean isConnected;
    @XmlTransient
    private EClientSocket eClientSocket; // null means not connected yet or manually disconnected

    public void clear() {
        accounts = null;
        isConnected = false;
        eClientSocket = null;
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public EClientSocket getClientSocket() {
        return eClientSocket;
    }

    public void setClientSocket(EClientSocket eClientSocket) {
        this.eClientSocket = eClientSocket;
    }
}
