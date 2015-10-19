package com.highpowerbear.hpbanalytics.iblogger.rest.model;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by robertk on 19.10.2015.
 */
public class IbOrderFilter {
    private Map<IbLoggerDefinitions.FilterOperatorString, String> symbolFilterMap = new HashMap<>();
    private Map<IbLoggerDefinitions.FilterOperatorEnum, Set<String>> secTypeFilterMap = new HashMap<>();
    private Map<IbLoggerDefinitions.FilterOperatorCalendar, Calendar> submitDateFilterMap = new HashMap<>();
    private Map<IbLoggerDefinitions.FilterOperatorEnum, Set<IbLoggerDefinitions.IbOrderStatus>> statusFilterMap = new HashMap<>();

    public Map<IbLoggerDefinitions.FilterOperatorString, String> getSymbolFilterMap() {
        return symbolFilterMap;
    }

    public Map<IbLoggerDefinitions.FilterOperatorEnum, Set<String>> getSecTypeFilterMap() {
        return secTypeFilterMap;
    }

    public Map<IbLoggerDefinitions.FilterOperatorCalendar, Calendar> getSubmitDateFilterMap() {
        return submitDateFilterMap;
    }

    public Map<IbLoggerDefinitions.FilterOperatorEnum, Set<IbLoggerDefinitions.IbOrderStatus>> getStatusFilterMap() {
        return statusFilterMap;
    }
}
