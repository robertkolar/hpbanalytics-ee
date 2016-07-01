package com.highpowerbear.hpbanalytics.c2.rest;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.rest.model.C2SignalFilter;
import com.highpowerbear.hpbanalytics.c2.rest.model.InputRequestFilter;

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
 * Created by robertk on 20.10.2015.
 */
@ApplicationScoped
public class FilterParser {

    public InputRequestFilter parseInputRequestFilter(String jsonFilter) {
        InputRequestFilter filter = new InputRequestFilter();
        if (jsonFilter != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonFilter));
            JsonArray array = jsonReader.readArray();
            for (int i = 0; i < array.size(); i++) {
                String property = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.PROPERTY.toString()).getString();

                if (C2Definitions.InputRequestFilterField.RECEIVED_DATE.getVarName().equals(property)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(parseString(array, i)));
                    filter.getReceivedDateFilterMap().put(parseOperatorCalendar(array, i), cal);

                } else if (C2Definitions.InputRequestFilterField.STATUS.getVarName().equals(property)) {
                    C2Definitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getStatusFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getStatusFilterMap().get(operator).add(C2Definitions.InputStatus.valueOf(v.toUpperCase()));
                    }

                } else if (C2Definitions.InputRequestFilterField.SYMBOL.getVarName().equals(property)) {
                    filter.getSymbolFilterMap().put(parseOperatorString(array, i), parseString(array, i));

                } else if (C2Definitions.InputRequestFilterField.SEC_TYPE.getVarName().equals(property)) {
                    C2Definitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getSecTypeFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getSecTypeFilterMap().get(operator).add(C2Definitions.SecType.valueOf(v.toUpperCase()));
                    }
                }
            }
            jsonReader.close();
        }
        return filter;
    }

    public C2SignalFilter parseC2SignalFilter(String jsonFilter) {
        C2SignalFilter filter = new C2SignalFilter();
        if (jsonFilter != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonFilter));
            JsonArray array = jsonReader.readArray();
            for (int i = 0; i < array.size(); i++) {
                String property = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.PROPERTY.toString()).getString();

                if (C2Definitions.C2SignalFilterField.CREATED_DATE.getVarName().equals(property)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.valueOf(parseString(array, i)));
                    filter.getCreatedDateFilterMap().put(parseOperatorCalendar(array, i), cal);

                } else if (C2Definitions.C2SignalFilterField.PUBLISH_STATUS.getVarName().equals(property)) {
                    C2Definitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getPublishStatusFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getPublishStatusFilterMap().get(operator).add(C2Definitions.PublishStatus.valueOf(v.toUpperCase()));
                    }

                } else if (C2Definitions.C2SignalFilterField.POLL_STATUS.getVarName().equals(property)) {
                    C2Definitions.FilterOperatorEnum operator = parseOperatorEnum(array, i);
                    filter.getPollStatusFilterMap().put(operator, new HashSet<>());
                    for (String v : parseValues(array, i)) {
                        filter.getPollStatusFilterMap().get(operator).add(C2Definitions.PollStatus.valueOf(v.toUpperCase()));
                    }

                } else if (C2Definitions.C2SignalFilterField.SYMBOL.getVarName().equals(property)) {
                    filter.getSymbolFilterMap().put(parseOperatorString(array, i), parseString(array, i));
                }
            }
            jsonReader.close();
        }
        return filter;
    }

    private String parseString(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.VALUE.toString()).getString();
    }

    private Long parseLong(JsonArray array, int i) {
        return array.getJsonObject(i).getJsonNumber(C2Definitions.FilterKey.VALUE.toString()).longValue();
    }

    private Set<String> parseValues(JsonArray array, int i) {
        JsonArray a = array.getJsonObject(i).getJsonArray(C2Definitions.FilterKey.VALUE.toString());
        Set<String> values = new HashSet<>();
        for (int j = 0; j < a.size(); j++) {
            values.add(a.getString(j));
        }
        return values;
    }

    private C2Definitions.FilterOperatorString parseOperatorString(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.OPERATOR.toString());
        C2Definitions.FilterOperatorString operator;
        try {
            operator = (json != null ? C2Definitions.FilterOperatorString.valueOf(json.getString().toUpperCase()) : C2Definitions.FilterOperatorString.LIKE);
        } catch (Exception e) {
            operator = C2Definitions.FilterOperatorString.LIKE;
        }
        return operator;
    }

    private C2Definitions.FilterOperatorNumber parseOperatorNumber(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.OPERATOR.toString());
        C2Definitions.FilterOperatorNumber operator;
        try {
            operator = (json != null ? C2Definitions.FilterOperatorNumber.valueOf(json.getString().toUpperCase()) : C2Definitions.FilterOperatorNumber.EQ);
        } catch (Exception e) {
            operator = C2Definitions.FilterOperatorNumber.EQ;
        }
        return operator;
    }

    private C2Definitions.FilterOperatorCalendar parseOperatorCalendar(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.OPERATOR.toString());
        C2Definitions.FilterOperatorCalendar operator;
        try {
            operator = (json != null ? C2Definitions.FilterOperatorCalendar.valueOf(json.getString().toUpperCase()) : C2Definitions.FilterOperatorCalendar.EQ);
        } catch (Exception e) {
            operator = C2Definitions.FilterOperatorCalendar.EQ;
        }
        return operator;
    }

    private C2Definitions.FilterOperatorEnum parseOperatorEnum(JsonArray array, int i) {
        JsonString json = array.getJsonObject(i).getJsonString(C2Definitions.FilterKey.OPERATOR.toString());
        C2Definitions.FilterOperatorEnum operator;
        try {
            operator = (json != null ? C2Definitions.FilterOperatorEnum.valueOf(json.getString().toUpperCase()) : C2Definitions.FilterOperatorEnum.IN);
        } catch (Exception e) {
            operator = C2Definitions.FilterOperatorEnum.IN;
        }
        return operator;
    }
}
