package com.highpowerbear.hpbanalytics.c2.entity;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by robertk on 3/29/15.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "publishevent", schema = "c2", catalog = "hpbanalytics")
public class PublishEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="publishevent", table="sequence", schema = "c2", catalog = "hpbanalytics", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="publishevent")
    private Long id;
    @XmlTransient
    @ManyToOne
    private C2Signal c2Signal;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @Enumerated(EnumType.STRING)
    private C2Definitions.PublishStatus status;
    private String c2Request;
    private String c2Response;

    @XmlElement
    public Long getC2SignalDbId() {
        return c2Signal.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublishEvent that = (PublishEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public C2Signal getC2Signal() {
        return c2Signal;
    }

    public void setC2Signal(C2Signal c2Signal) {
        this.c2Signal = c2Signal;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public C2Definitions.PublishStatus getStatus() {
        return status;
    }

    public void setStatus(C2Definitions.PublishStatus status) {
        this.status = status;
    }

    public String getC2Request() {
        return c2Request;
    }

    public void setC2Request(String c2RestRequest) {
        this.c2Request = c2RestRequest;
    }

    public String getC2Response() {
        return c2Response;
    }

    public void setC2Response(String c2RestResponse) {
        this.c2Response = c2RestResponse;
    }
}
