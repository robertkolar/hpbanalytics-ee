package com.highpowerbear.hpbanalytics.report.rest;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.rest.model.ExecutionFilter;
import com.highpowerbear.hpbanalytics.report.rest.model.TradeFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.StringReader;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertk on 21.10.2015.
 */
@Named
@ApplicationScoped
public class FilterParser {

    public ExecutionFilter parseExecutionFilter(String jsonFilter) {
        ExecutionFilter filter = new ExecutionFilter();
        if (jsonFilter != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonFilter));
            JsonArray array = jsonReader.readArray();
            for (int i = 0; i < array.size(); i++) {
                String property = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.PROPERTY.toString()).getString();

                if (ReportDefinitions.ExecutionFilterField.SYMBOL.getVarName().equals(property)) {
                    filter.getSymbolFilterMap().put(parseOperatorString(array, i), parseString(array, i));

                } else if (ReportDefinitions.ExecutionFilterField.SEC_TYPE.getVarName().equals(property)) {
                    ReportDefinitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getSecTypeFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getSecTypeFilterMap().get(operator).add(v.toUpperCase());
                    }

                } else if (ReportDefinitions.ExecutionFilterField.FILL_DATE.getVarName().equals(property)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(parseString(array, i)));
                    filter.getFillDateFilterMap().put(parseOperatorCalendar(array, i), cal);
                }
            }
            jsonReader.close();
        }
        return filter;
    }

    public TradeFilter parseTradeFilter(String jsonFilter) {
        TradeFilter filter = new TradeFilter();
        if (jsonFilter != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonFilter));
            JsonArray array = jsonReader.readArray();
            for (int i = 0; i < array.size(); i++) {
                String property = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.PROPERTY.toString()).getString();

                if (ReportDefinitions.TradeFilterField.SYMBOL.getVarName().equals(property)) {
                    filter.getSymbolFilterMap().put(parseOperatorString(array, i), parseString(array, i));

                } else if (ReportDefinitions.TradeFilterField.SEC_TYPE.getVarName().equals(property)) {
                    ReportDefinitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getSecTypeFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getSecTypeFilterMap().get(operator).add(v.toUpperCase());
                    }

                } else if (ReportDefinitions.TradeFilterField.OPEN_DATE.getVarName().equals(property)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(parseString(array, i)));
                    filter.getOpenDateFilterMap().put(parseOperatorCalendar(array, i), cal);
                }
            }
            jsonReader.close();
        }
        return filter;
    }

    private String parseString(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.VALUE.toString()).getString();
    }

    private Long parseLong(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonNumber(ReportDefinitions.FilterKey.VALUE.toString()).longValue();
    }

    private Set<String> parseValues(JsonArray array, int i) {
        JsonArray a = array.getJsonObject(i).getJsonArray(ReportDefinitions.FilterKey.VALUE.toString());
        Set<String> values = new HashSet<>();
        for (int j = 0; j < a.size(); j++) {
            values.add(a.getString(j));
        }
        return values;
    }

    private ReportDefinitions.FilterOperatorString parseOperatorString(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.OPERATOR.toString());
        ReportDefinitions.FilterOperatorString operator;
        try {
            operator = (json != null ? ReportDefinitions.FilterOperatorString.valueOf(json.getString().toUpperCase()) : ReportDefinitions.FilterOperatorString.LIKE);
        } catch (Exception e) {
            operator = ReportDefinitions.FilterOperatorString.LIKE;
        }
        return operator;
    }

    private ReportDefinitions.FilterOperatorNumber parseOperatorNumber(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.OPERATOR.toString());
        ReportDefinitions.FilterOperatorNumber operator;
        try {
            operator = (json != null ? ReportDefinitions.FilterOperatorNumber.valueOf(json.getString().toUpperCase()) : ReportDefinitions.FilterOperatorNumber.EQ);
        } catch (Exception e) {
            operator = ReportDefinitions.FilterOperatorNumber.EQ;
        }
        return operator;
    }

    private ReportDefinitions.FilterOperatorCalendar parseOperatorCalendar(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.OPERATOR.toString());
        ReportDefinitions.FilterOperatorCalendar operator;
        try {
            operator = (json != null ? ReportDefinitions.FilterOperatorCalendar.valueOf(json.getString().toUpperCase()) : ReportDefinitions.FilterOperatorCalendar.EQ);
        } catch (Exception e) {
            operator = ReportDefinitions.FilterOperatorCalendar.EQ;
        }
        return operator;
    }

    private ReportDefinitions.FilterOperatorEnum parseOperatorEnum(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(ReportDefinitions.FilterKey.OPERATOR.toString());
        ReportDefinitions.FilterOperatorEnum operator;
        try {
            operator = (json != null ? ReportDefinitions.FilterOperatorEnum.valueOf(json.getString().toUpperCase()) : ReportDefinitions.FilterOperatorEnum.IN);
        } catch (Exception e) {
            operator = ReportDefinitions.FilterOperatorEnum.IN;
        }
        return operator;
    }
}
