package com.highpowerbear.hpbanalytics.report.cdibean;

import com.highpowerbear.hpbanalytics.report.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.Execution;
import com.highpowerbear.hpbanalytics.report.process.RepProcessor;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author robertk
 */
@Named
@RequestScoped
public class NewExecutionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject private ExecutionBean executionBean;
    @Inject private RepProcessor repProcessor;
    private Execution newExecution;
    private List<SelectItem> actionItems;
    private List<SelectItem> secTypeItems;
    private Date date;
    private Date maxDate;
    
    @PostConstruct
    public void init() {
        maxDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime();
        newExecution = new Execution();
        newExecution.setQuantity(100);
        newExecution.setAction(RepDefinitions.Action.BUY);
        newExecution.setFillPrice(0.00);
        newExecution.setSecType(RepDefinitions.SecType.STK);
        newExecution.setUnderlying("SPY");
        newExecution.setSymbol("SPY");
        this.date = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime();
        actionItems = new ArrayList<>();
        actionItems.add(new SelectItem(RepDefinitions.Action.BUY, RepDefinitions.Action.BUY.toString()));
        actionItems.add(new SelectItem(RepDefinitions.Action.SELL, RepDefinitions.Action.SELL.toString()));
        secTypeItems = new ArrayList<>();
        secTypeItems.add(new SelectItem(RepDefinitions.SecType.STK, RepDefinitions.SecType.STK.toString()));
        secTypeItems.add(new SelectItem(RepDefinitions.SecType.OPT, RepDefinitions.SecType.OPT.toString()));
        secTypeItems.add(new SelectItem(RepDefinitions.SecType.FUT, RepDefinitions.SecType.FUT.toString()));
        secTypeItems.add(new SelectItem(RepDefinitions.SecType.CASH, RepDefinitions.SecType.CASH.toString()));
    }
    
    public void submit() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        c.setTimeInMillis(date.getTime());
        newExecution.setFillDate(c);
        newExecution.setReceivedDate(Calendar.getInstance());
        newExecution.setReport(executionBean.getReport());
        newExecution.setOrigin(RepDefinitions.ORIGIN_MANUAL);
        newExecution.setReferenceId(RepDefinitions.NA);
        repProcessor.newExecution(newExecution);
        executionBean.setShowNewExecution(Boolean.FALSE);
        executionBean.refresh();
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SelectItem> getActionItems() {
        return actionItems;
    }

    public Execution getNewExecution() {
        return newExecution;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public List<SelectItem> getSecTypeItems() {
        return secTypeItems;
    }
}
