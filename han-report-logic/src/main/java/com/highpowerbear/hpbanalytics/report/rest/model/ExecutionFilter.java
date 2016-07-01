package com.highpowerbear.hpbanalytics.report.rest.model;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by robertk on 21.10.2015.
 */
public class ExecutionFilter {
    private Map<ReportDefinitions.FilterOperatorString, String> symbolFilterMap = new HashMap<>();
    private Map<ReportDefinitions.FilterOperatorEnum, Set<ReportDefinitions.SecType>> secTypeFilterMap = new HashMap<>();
    private Map<ReportDefinitions.FilterOperatorCalendar, Calendar> fillDateFilterMap = new HashMap<>();

    public Map<ReportDefinitions.FilterOperatorString, String> getSymbolFilterMap() {
        return symbolFilterMap;
    }

    public Map<ReportDefinitions.FilterOperatorEnum, Set<ReportDefinitions.SecType>> getSecTypeFilterMap() {
        return secTypeFilterMap;
    }

    public Map<ReportDefinitions.FilterOperatorCalendar, Calendar> getFillDateFilterMap() {
        return fillDateFilterMap;
    }
}
