package com.highpowerbear.hpbanalytics.c2.rest.model;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by robertk on 20.10.2015.
 */
public class C2SignalFilter {
    private Map<C2Definitions.FilterOperatorCalendar, Calendar> createdDateFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.PublishStatus>> publishStatusFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.PollStatus>> pollStatusFilterMap = new HashMap<>();
    private Map<C2Definitions.FilterOperatorString, String> symbolFilterMap = new HashMap<>();

    public Map<C2Definitions.FilterOperatorCalendar, Calendar> getCreatedDateFilterMap() {
        return createdDateFilterMap;
    }

    public Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.PublishStatus>> getPublishStatusFilterMap() {
        return publishStatusFilterMap;
    }

    public Map<C2Definitions.FilterOperatorEnum, Set<C2Definitions.PollStatus>> getPollStatusFilterMap() {
        return pollStatusFilterMap;
    }

    public Map<C2Definitions.FilterOperatorString, String> getSymbolFilterMap() {
        return symbolFilterMap;
    }
}
