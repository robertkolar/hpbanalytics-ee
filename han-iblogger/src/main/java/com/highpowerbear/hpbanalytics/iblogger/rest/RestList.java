package com.highpowerbear.hpbanalytics.iblogger.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by robertk on 14.1.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestList<T> {
    private List<T> items;
    private Long total;

    public RestList(List<T> items, Long total) {
        this.items = items;
        this.total = total;
    }

    public Long getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }
}
