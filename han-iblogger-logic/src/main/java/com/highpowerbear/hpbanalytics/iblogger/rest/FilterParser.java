package com.highpowerbear.hpbanalytics.iblogger.rest;

import com.highpowerbear.hpbanalytics.iblogger.common.IbLoggerDefinitions;
import com.highpowerbear.hpbanalytics.iblogger.rest.model.IbOrderFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.StringReader;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertk on 19.10.2015.
 */
@ApplicationScoped
public class FilterParser {

    public IbOrderFilter parseIbOrderFilter(String jsonFilter) {
        IbOrderFilter filter = new IbOrderFilter();
        if (jsonFilter != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonFilter));
            JsonArray array = jsonReader.readArray();
            for (int i = 0; i < array.size(); i++) {
                String property = array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.PROPERTY.toString()).getString();

                if (IbLoggerDefinitions.IbOrderFilterField.SYMBOL.getVarName().equals(property)) {
                    filter.getSymbolFilterMap().put(parseOperatorString(array, i), parseString(array, i));

                } else if (IbLoggerDefinitions.IbOrderFilterField.SEC_TYPE.getVarName().equals(property)) {
                    IbLoggerDefinitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getSecTypeFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getSecTypeFilterMap().get(operator).add(v.toUpperCase());
                    }

                } else if (IbLoggerDefinitions.IbOrderFilterField.SUBMIT_DATE.getVarName().equals(property)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(parseString(array, i)));
                    filter.getSubmitDateFilterMap().put(parseOperatorCalendar(array, i), cal);

                } else if (IbLoggerDefinitions.IbOrderFilterField.STATUS.getVarName().equals(property)) {
                    IbLoggerDefinitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getStatusFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getStatusFilterMap().get(operator).add(IbLoggerDefinitions.IbOrderStatus.valueOf(v.toUpperCase()));
                    }
                }
            }
            jsonReader.close();
        }
        return filter;
    }

    private String parseString(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.VALUE.toString()).getString();
    }

    private Long parseLong(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonNumber(IbLoggerDefinitions.FilterKey.VALUE.toString()).longValue();
    }

    private Set<String> parseValues(JsonArray array, int i) {
        JsonArray a = array.getJsonObject(i).getJsonArray(IbLoggerDefinitions.FilterKey.VALUE.toString());
        Set<String> values = new HashSet<>();
        for (int j = 0; j < a.size(); j++) {
            values.add(a.getString(j));
        }
        return values;
    }

    private IbLoggerDefinitions.FilterOperatorString parseOperatorString(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.OPERATOR.toString());
        IbLoggerDefinitions.FilterOperatorString operator;
        try {
            operator = (json != null ? IbLoggerDefinitions.FilterOperatorString.valueOf(json.getString().toUpperCase()) : IbLoggerDefinitions.FilterOperatorString.LIKE);
        } catch (Exception e) {
            operator = IbLoggerDefinitions.FilterOperatorString.LIKE;
        }
        return operator;
    }

    private IbLoggerDefinitions.FilterOperatorNumber parseOperatorNumber(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.OPERATOR.toString());
        IbLoggerDefinitions.FilterOperatorNumber operator;
        try {
            operator = (json != null ? IbLoggerDefinitions.FilterOperatorNumber.valueOf(json.getString().toUpperCase()) : IbLoggerDefinitions.FilterOperatorNumber.EQ);
        } catch (Exception e) {
            operator = IbLoggerDefinitions.FilterOperatorNumber.EQ;
        }
        return operator;
    }

    private IbLoggerDefinitions.FilterOperatorCalendar parseOperatorCalendar(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.OPERATOR.toString());
        IbLoggerDefinitions.FilterOperatorCalendar operator;
        try {
            operator = (json != null ? IbLoggerDefinitions.FilterOperatorCalendar.valueOf(json.getString().toUpperCase()) : IbLoggerDefinitions.FilterOperatorCalendar.EQ);
        } catch (Exception e) {
            operator = IbLoggerDefinitions.FilterOperatorCalendar.EQ;
        }
        return operator;
    }

    private IbLoggerDefinitions.FilterOperatorEnum parseOperatorEnum(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(IbLoggerDefinitions.FilterKey.OPERATOR.toString());
        IbLoggerDefinitions.FilterOperatorEnum operator;
        try {
            operator = (json != null ? IbLoggerDefinitions.FilterOperatorEnum.valueOf(json.getString().toUpperCase()) : IbLoggerDefinitions.FilterOperatorEnum.IN);
        } catch (Exception e) {
            operator = IbLoggerDefinitions.FilterOperatorEnum.IN;
        }
        return operator;
    }
}
