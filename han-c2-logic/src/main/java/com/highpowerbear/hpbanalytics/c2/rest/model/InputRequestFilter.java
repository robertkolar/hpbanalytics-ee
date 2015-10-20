package com.highpowerbear.hpbanalytics.c2.rest.model;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by robertk on 20.10.2015.
 */
public class InputRequestFilter {
    private Map<C2Definitions.FilterOperatorCalendar, Calendar> receivedDateFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.InputStatus>> statusFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorString, String> symbolFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.SecType>> secTypeFilterMap = new HashMap<>();

    public Map<C2Definitions.FilterOperatorCalendar, Calendar> getReceivedDateFilterMap() {
        return receivedDateFilterMap;
    }

    public Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.InputStatus>> getStatusFilterMap() {
        return statusFilterMap;
    }

    public Map<C2Definitions.FilterOperatorString, String> getSymbolFilterMap() {
        return symbolFilterMap;
    }

    public Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.SecType>> getSecTypeFilterMap() {
        return secTypeFilterMap;
    }
}
