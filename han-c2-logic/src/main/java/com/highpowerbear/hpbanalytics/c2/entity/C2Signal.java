package com.highpowerbear.hpbanalytics.c2.entity;

import com.highpowerbear.hpbanalytics.c2.c2client.C2ApiEnums;
import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Robert
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "c2signal", schema = "c2", catalog = "hpbanalytics")
public class C2Signal implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="c2signal", table="sequence", schema = "c2", catalog = "hpbanalytics", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="c2signal")
    private Long id;
    private String origin; // in case of IB origin --> IB:ibAccountId
    private String referenceId; // in case of IB origin --> permId
    @XmlTransient
    @ManyToOne
    private C2System c2System;
    private Integer c2SignalId;
    @Enumerated(EnumType.STRING)
    private C2ApiEnums.Action action;
    private Integer quant;
    private Integer reversalQuant = 0;
    @Enumerated(EnumType.STRING)
    private C2Definitions.ReversalSignalType reversalSignalType;
    private Integer reversalParent;
    private String symbol;
    @Enumerated(EnumType.STRING)
    private C2ApiEnums.Instrument instrument;
    private Double limitPrice;
    private Double stopPrice;
    @Enumerated(EnumType.STRING)
    private C2ApiEnums.Duration duration;
    private Integer ocaGroup;
    @Enumerated(EnumType.STRING)
    private C2Definitions.PollStatus pollStatus;
    @Enumerated(EnumType.STRING)
    private C2Definitions.PublishStatus publishStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar pollDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar publishStatusDate;
    private Double tradePrice;
    @OneToMany(mappedBy = "c2Signal", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate DESC, id DESC")
    private List<PublishEvent> publishEvents;

    @XmlElement
    public Integer getC2SystemId() {
        return c2System.getSystemId();
    }

    public void addPublishEvent(PublishEvent publishEvent) {
        this.publishStatus = publishEvent.getStatus();
        this.publishStatusDate = publishEvent.getEventDate();
        if (publishEvents == null) {
            publishEvents = new ArrayList<>();
        }
        publishEvents.add(publishEvent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C2Signal c2Signal = (C2Signal) o;

        return !(id != null ? !id.equals(c2Signal.id) : c2Signal.id != null);

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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public C2System getC2System() {
        return c2System;
    }

    public void setC2System(C2System c2System) {
        this.c2System = c2System;
    }

    public Integer getC2SignalId() {
        return c2SignalId;
    }

    public void setC2SignalId(Integer c2SignalId) {
        this.c2SignalId = c2SignalId;
    }

    public C2ApiEnums.Action getAction() {
        return action;
    }

    public void setAction(C2ApiEnums.Action action) {
        this.action = action;
    }

    public Integer getQuant() {
        return quant;
    }

    public void setQuant(Integer quant) {
        this.quant = quant;
    }

    public Integer getReversalQuant() {
        return reversalQuant;
    }

    public void setReversalQuant(Integer reversalQuant) {
        this.reversalQuant = reversalQuant;
    }

    public C2Definitions.ReversalSignalType getReversalSignalType() {
        return reversalSignalType;
    }

    public void setReversalSignalType(C2Definitions.ReversalSignalType reversalSignalType) {
        this.reversalSignalType = reversalSignalType;
    }

    public Integer getReversalParent() {
        return reversalParent;
    }

    public void setReversalParent(Integer reversalConditionalUpon) {
        this.reversalParent = reversalConditionalUpon;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public C2ApiEnums.Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(C2ApiEnums.Instrument instrument) {
        this.instrument = instrument;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limit) {
        this.limitPrice = limit;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stop) {
        this.stopPrice = stop;
    }

    public C2ApiEnums.Duration getDuration() {
        return duration;
    }

    public void setDuration(C2ApiEnums.Duration duration) {
        this.duration = duration;
    }

    public Integer getOcaGroup() {
        return ocaGroup;
    }

    public void setOcaGroup(Integer ocaGroup) {
        this.ocaGroup = ocaGroup;
    }

    public C2Definitions.PollStatus getPollStatus() {
        return pollStatus;
    }

    public void setPollStatus(C2Definitions.PollStatus pollStatus) {
        this.pollStatus = pollStatus;
    }

    public C2Definitions.PublishStatus getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(C2Definitions.PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar dateCreated) {
        this.createdDate = dateCreated;
    }

    public Calendar getPollDate() {
        return pollDate;
    }

    public void setPollDate(Calendar pollStatusDate) {
        this.pollDate = pollStatusDate;
    }

    public Calendar getPublishStatusDate() {
        return publishStatusDate;
    }

    public void setPublishStatusDate(Calendar publishStatusDate) {
        this.publishStatusDate = publishStatusDate;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public List<PublishEvent> getPublishEvents() {
        return publishEvents;
    }

    public void setPublishEvents(List<PublishEvent> publishEvents) {
        this.publishEvents = publishEvents;
    }
}
