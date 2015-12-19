package com.highpowerbear.hpbanalytics.c2.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by robertk on 3/7/15.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="c2_c2system")
public class C2System implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer systemId;
    private String systemName;
    private String origin; // in case of IB origin --> IB:ibAccountId
    private boolean stk;
    private boolean fut;
    private boolean opt;
    private boolean fx;
    private String email;
    @XmlTransient
    private String password;
    private boolean useSsl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C2System c2System = (C2System) o;

        return !(systemId != null ? !systemId.equals(c2System.systemId) : c2System.systemId != null);

    }

    @Override
    public int hashCode() {
        return systemId != null ? systemId.hashCode() : 0;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String conversionOrigin) {
        this.origin = conversionOrigin;
    }

    public boolean isStk() {
        return stk;
    }

    public void setStk(boolean stk) {
        this.stk = stk;
    }

    public boolean isFut() {
        return fut;
    }

    public void setFut(boolean fut) {
        this.fut = fut;
    }

    public boolean isOpt() {
        return opt;
    }

    public void setOpt(boolean opt) {
        this.opt = opt;
    }

    public boolean isFx() {
        return fx;
    }

    public void setFx(boolean fx) {
        this.fx = fx;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }
}
