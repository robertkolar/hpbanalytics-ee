package com.highpowerbear.hpbanalytics.reports.cdibean;

import com.highpowerbear.hpbanalytics.reports.entity.Execution;
import com.highpowerbear.hpbanalytics.reports.entity.Report;
import com.highpowerbear.hpbanalytics.reports.persistence.RepDao;
import com.highpowerbear.hpbanalytics.reports.process.RepProcessor;
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
public class ExecutionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject private RepDao repDao;
    @Inject private RepProcessor repProcessor;
    private List<Execution> executions;
    private Execution[] selectedExecutions;
    private Boolean showNewExecution = false;
    private Report report;
    private List<ExecutionBean> filteredExecution;
    
    public void setReport(Report report) {
        this.report = report;
        refresh();
        
    }
    
    public Report getReport() {
        return report;
    }
    
    public void refresh() {
        if (report != null) {
            executions = repDao.getExecutions(report, true);
        }
    }
    
    public Execution[] getSelectedExecutions() {
        return selectedExecutions;  
    }
    
    public void setSelectedExecutions(Execution[] selectedExecutions) {
        this.selectedExecutions = selectedExecutions;  
    }
    
    public void setShowNewExecution(Boolean showNewExecution) {
        this.showNewExecution = showNewExecution;
    }

    public Boolean getShowNewExecution() {
        return showNewExecution;
    }
    
    public List<Execution> getExecutions() {
        return executions;
    }
     
    public void deleteSelectedEcecutions() {
        repProcessor.deleteSelectedEcecutions(selectedExecutions);
        refresh();
    }

    public List<ExecutionBean> getFilteredExecution() {
        return filteredExecution;
    }

    public void setFilteredExecution(List<ExecutionBean> filteredExecution) {
        this.filteredExecution = filteredExecution;
    }
}