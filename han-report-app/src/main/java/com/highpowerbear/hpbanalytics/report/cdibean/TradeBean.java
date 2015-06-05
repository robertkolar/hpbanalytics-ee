package com.highpowerbear.hpbanalytics.report.cdibean;


import com.highpowerbear.hpbanalytics.report.entity.Report;
import com.highpowerbear.hpbanalytics.report.entity.Trade;
import com.highpowerbear.hpbanalytics.report.persistence.RepDao;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author robertk
 */
@Named
@SessionScoped
public class TradeBean implements Serializable  {
    private static final long serialVersionUID = 1L;
    
    @Inject private RepDao repDao;
    private List<Trade> trades;
    private Report report;
    private List<Trade> filteredTrades;
    
    public void setReport(Report report) {
        this.report = report;
        refresh();
        
    }

    public Report getReport() {
        return report;
    }
    
    public void refresh() {
        if (report != null) {
            trades = repDao.getTrades(report, true);
        }
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<Trade> getFilteredTrades() {
        return filteredTrades;
    }

    public void setFilteredTrades(List<Trade> filteredTrades) {
        this.filteredTrades = filteredTrades;
    }
}
