package com.highpowerbear.hpbanalytics.c2.persistence;

import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.entity.PollEvent;
import com.highpowerbear.hpbanalytics.c2.rest.model.C2SignalFilter;
import com.highpowerbear.hpbanalytics.c2.rest.model.InputRequestFilter;

import java.util.List;

/**
 *
 * @author Robert
 */
public interface C2Dao {
    C2System getC2SystemByConversionOrigin(String conversionOrigin);
    C2System findC2System(Integer systemId);
    List<C2System> getC2Systems();
    C2System updateC2System(C2System c2System);
    void newC2Signal(C2Signal signal);
    C2Signal updateC2Signal(C2Signal signal);
    C2Signal findC2Signal(Long dbId);
    List<C2Signal> getC2SignalsToPoll(C2System c2System);
    List<C2Signal> getC2SignalsByOriginReference(String origin, String reference);
    List<C2Signal> getFilteredC2Signals(C2System c2System, C2SignalFilter filter, Integer start, Integer limit);
    Long getNumFilteredC2Signals(C2System c2System, C2SignalFilter filter);
    void newPollEvent(PollEvent pollEvent);
    List<PollEvent> getPollEvents(Long csSignaldbId, Integer start, Integer limit);
    Long getNumPollEvents(Long c2SignaldbId);
    Long getPollErrorCount(C2Signal c2Signal);
    void newInputRequest(InputRequest inputRequest);
    void updateInputRequest(InputRequest inputRequest);
    List<InputRequest> getFilteredInputRequests(InputRequestFilter filter, Integer start, Integer limit);
    Long getNumFilteredInputRequests(InputRequestFilter filter);
}