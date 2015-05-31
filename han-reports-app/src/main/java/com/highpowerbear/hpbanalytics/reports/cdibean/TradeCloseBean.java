package com.highpowerbear.hpbanalytics.reports.cdibean;

import com.highpowerbear.hpbanalytics.reports.common.RepDefinitions;
import com.highpowerbear.hpbanalytics.reports.entity.Execution;
import com.highpowerbear.hpbanalytics.reports.entity.Trade;
import com.highpowerbear.hpbanalytics.reports.process.OptionParser;
import com.highpowerbear.hpbanalytics.reports.process.RepProcessor;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@SessionScoped
public class TradeCloseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(RepDefinitions.LOGGER);
    @Inject private TradeBean tradeBean;
    @Inject private OptionParser optionParser;
    @Inject private RepProcessor repProcessor;
    private Execution execution;
    private Execution childExecution;
    private Date minDate;
    private Date maxDate;
    private Date date;
    private Boolean isDateDisabled;
    private Boolean isPriceDisabled;
    private List<SelectItem> optionCloseTypeItems;
    private RepDefinitions.OptionCloseType optionCloseType;
    private NumberFormat nf;

    @PostConstruct
    public void postConstruct() {
        nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(4);
        optionCloseTypeItems = new ArrayList<>();
    }

    public void closeTrade(Trade t) {
        execution = null;
        childExecution = null;
        l.info("Trade to close: " + t.print());
        optionCloseType = RepDefinitions.OptionCloseType.CLOSE;
        isDateDisabled = false;
        isPriceDisabled = false;
        execution = new Execution();
        execution.setReport(tradeBean.getReport());
        execution.setOrigin(tradeBean.getReport().getOrigin());
        execution.setAction(t.getType() == RepDefinitions.TradeType.LONG ? RepDefinitions.Action.SELL : RepDefinitions.Action.BUY);
        execution.setUnderlying(t.getUnderlying());
        execution.setCurrency(t.getCurrency());
        execution.setSymbol(t.getSymbol());
        execution.setSecType(t.getSecType());
        execution.setQuantity((t.getOpenPosition() >= 0 ? t.getOpenPosition() : -t.getOpenPosition()));
        Calendar minC = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        minC.setTimeInMillis(t.getLastSplitExecution().getExecution().getFillDate().getTimeInMillis());
        minC.add(Calendar.DAY_OF_MONTH, +1);
        minDate = minC.getTime();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        l.info("Mindate: " + df.format(minDate));
        maxDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime();
        this.setDate(maxDate);
        execution.setFillPrice(0.0);
        if (execution.getSecType() == RepDefinitions.SecType.OPT) {
            try {
                optionParser.parse(execution.getSymbol());
                optionCloseTypeItems.clear();
                optionCloseTypeItems.add(new SelectItem(RepDefinitions.OptionCloseType.CLOSE, RepDefinitions.OptionCloseType.CLOSE.toString()));
                optionCloseTypeItems.add(new SelectItem(RepDefinitions.OptionCloseType.EXPIRE, RepDefinitions.OptionCloseType.EXPIRE.toString()));
                optionCloseTypeItems.add(execution.getAction() == RepDefinitions.Action.BUY ? new SelectItem(RepDefinitions.OptionCloseType.ASSIGN, RepDefinitions.OptionCloseType.ASSIGN.toString()) : new SelectItem(RepDefinitions.OptionCloseType.EXERCISE, RepDefinitions.OptionCloseType.EXERCISE.toString()));
            } catch (Exception exc) {
                l.log(Level.SEVERE, exc.getMessage(), exc);
                execution = null;
                return;
            }
            maxDate = optionParser.getExpDate();
            this.setDate(maxDate);
        }
    }

    public void submit() {
        l.info("Adding Execution: " + execution.print());
        repProcessor.newExecution(execution);
        if (childExecution != null) {
            l.info("Adding child Execution: " + childExecution.print());
            repProcessor.newExecution(childExecution);
        }
        execution = null;
        tradeBean.refresh();
    }
    
    public void cancel() {
        execution = null;
    }

    public void optionCloseTypeChanged() {
        l.info("Changed: " + optionCloseType);
        execution.setComment(optionCloseType.toString());
        childExecution = new Execution();
        childExecution.setReceivedDate(Calendar.getInstance());
        childExecution.setReport(tradeBean.getReport());
        childExecution.setOrigin(RepDefinitions.ORIGIN_MANUAL);
        childExecution.setReferenceId(RepDefinitions.NA);
        childExecution.setQuantity((OptionParser.isMini(execution.getSymbol()) ? 10 : 100) * execution.getQuantity());
        childExecution.setUnderlying(optionParser.getUnderlying());
        childExecution.setSymbol(optionParser.getUnderlying());
        childExecution.setSecType(RepDefinitions.SecType.STK);
        childExecution.setFillPrice(optionParser.getStrikePrice());
        childExecution.setComment(optionCloseType.toString());
        // introduce random offset for stocks that were purchased/sold as a result of assignment so in case of same symbol they don't get exactly the same date
        // this is required constraint for all executions for the same symbol and execution source, see Execution entity
        Random r = new Random();
        long randomLong = r.nextInt(59000);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        c.setTimeInMillis(date.getTime() + randomLong);
        childExecution.setFillDate(c);
        isPriceDisabled = true;
        isDateDisabled = true;
        switch (optionCloseType) {
            case CLOSE:
                isDateDisabled = false;
                isPriceDisabled = false;
                childExecution = null;
                break;
            case ASSIGN:
                this.setDate(maxDate);
                switch (optionParser.getOptType()) {
                    case PUT:
                        childExecution.setAction(RepDefinitions.Action.BUY);
                        break;
                    case CALL:
                        childExecution.setAction(RepDefinitions.Action.SELL);
                        break;
                }
                break;
            case EXERCISE:
                this.setDate(maxDate);
                switch (optionParser.getOptType()) {
                    case PUT:
                        childExecution.setAction(RepDefinitions.Action.SELL);
                        break;
                    case CALL:
                        childExecution.setAction(RepDefinitions.Action.BUY);
                        break;
                }
                break;
            case EXPIRE:
                this.setDate(maxDate);
                childExecution = null;
                break;
        }
    }

    public Boolean getRendered() {
        return (execution != null);
    }

    public Boolean getChildRendered() {
        return (childExecution != null);
    }

    public Boolean getOptionRendered() {
        return (execution.getSecType() == RepDefinitions.SecType.OPT);
    }

    public Execution getExecution() {
        return execution;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        c.setTimeInMillis(date.getTime());
        this.execution.setFillDate(c);
        this.date = date;
    }

    public List<SelectItem> getOptionCloseTypeItems() {
        return optionCloseTypeItems;
    }

    public RepDefinitions.OptionCloseType getOptionCloseType() {
        return optionCloseType;
    }

    public void setOptionCloseType(RepDefinitions.OptionCloseType optionCloseType) {
        this.optionCloseType = optionCloseType;
    }

    public void setPrice(Double pr) {
        Double prF = Double.valueOf(nf.format(pr));
        if (execution != null) {
            execution.setFillPrice(prF);
        }
    }

    public Double getPrice() {
        return (execution != null ? execution.getFillPrice() : null);
    }

    public Execution getChildExecution() {
        return childExecution;
    }

    public Boolean getIsDateDisabled() {
        return isDateDisabled;
    }

    public Boolean getIsPriceDisabled() {
        return isPriceDisabled;
    }
}
