package com.highpowerbear.hpbanalytics.c2.conversion;

import com.highpowerbear.hpbanalytics.c2.c2client.C2ApiEnums;
import com.highpowerbear.hpbanalytics.c2.c2client.RequestHandler;
import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by rkolar on 2/7/14.
 */
@Named
@ApplicationScoped
public class SignalCreator {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @Inject private C2Dao c2Dao;
    @Inject private RequestHandler requestHandler;

    public C2Signal create(C2System c2System, InputRequest inputRequest) {
        C2Signal c2Signal = new C2Signal();
        c2Signal.setOrigin(inputRequest.getOrigin());
        c2Signal.setReferenceId(inputRequest.getReferenceId());
        c2Signal.setC2System(c2System);
        c2Signal.setCreatedDate(Calendar.getInstance());
        c2Signal.setPollStatus(C2Definitions.PollStatus.NOTPOLLED);
        c2Signal.setLimitPrice(C2Definitions.OrderType.LMT.equals(inputRequest.getOrderType()) ? inputRequest.getOrderPrice() : 0d);
        c2Signal.setStopPrice(C2Definitions.OrderType.STP.equals(inputRequest.getOrderType()) ? inputRequest.getOrderPrice() : 0d);
        determineInstrument(c2Signal, inputRequest);
        determineSymbol(c2Signal, inputRequest);
        c2Dao.newC2Signal(c2Signal);
        Integer position = requestHandler.getPosition(c2Signal);
        c2Signal = c2Dao.findC2Signal(c2Signal.getId()); // refresh from DB to prevent adding multiple events
        determineQuant(c2Signal, position, inputRequest);
        determineAction(c2Signal, position, inputRequest);
        determineDuration(c2Signal, inputRequest);
        c2Signal = c2Dao.updateC2Signal(c2Signal);
        return c2Signal;
    }

    public C2Signal createReversal(C2Signal s1) {
        C2ApiEnums.Action action2 = (C2ApiEnums.Action.BTC.equals(s1.getAction()) ? C2ApiEnums.Action.BTO : C2ApiEnums.Action.STO);
        C2Signal s2 = new C2Signal();
        s2.setOrigin(s1.getOrigin());
        s2.setReferenceId(s1.getReferenceId());
        s2.setC2System(s1.getC2System());
        s2.setCreatedDate(s1.getCreatedDate());
        s2.setPollStatus(C2Definitions.PollStatus.NOTPOLLED);
        s2.setAction(action2);
        s2.setQuant(s1.getReversalQuant());
        s2.setReversalQuant(0);
        s2.setReversalSignalType(C2Definitions.ReversalSignalType.CHILD);
        s2.setSymbol(s1.getSymbol());
        s2.setInstrument(s1.getInstrument());
        s2.setStopPrice(0d);
        s2.setLimitPrice(0d);
        s2.setDuration(s1.getDuration());
        s2.setReversalParent(s1.getC2SignalId());
        c2Dao.newC2Signal(s2);
        return s2;
    }

    public C2Signal createUpdated(C2Signal s1, InputRequest inputRequest) {
        C2Signal s2 = new C2Signal();
        s2.setOrigin(inputRequest.getOrigin());
        s2.setReferenceId(inputRequest.getReferenceId());
        s2.setC2System(s1.getC2System());
        s2.setCreatedDate(Calendar.getInstance());
        s2.setPollStatus(C2Definitions.PollStatus.NOTPOLLED);
        s2.setLimitPrice(s1.getLimitPrice() == 0d ? 0d : inputRequest.getOrderPrice());
        s2.setStopPrice(s1.getStopPrice() == 0d ? 0d : inputRequest.getOrderPrice());
        s2.setAction(s1.getAction());
        s2.setQuant(s1.getQuant());
        s2.setReversalQuant(s1.getReversalQuant());
        s2.setReversalSignalType(s1.getReversalSignalType());
        s2.setSymbol(s1.getSymbol());
        s2.setInstrument(s1.getInstrument());
        s2.setDuration(s1.getDuration());
        c2Dao.newC2Signal(s2);
        return s2;
    }

    private void determineInstrument(C2Signal c2Signal, InputRequest inputRequest) {
        C2ApiEnums.Instrument instrument = null;
        if (C2Definitions.SecType.STK.equals(inputRequest.getSecType())) {
            instrument = C2ApiEnums.Instrument.STOCK;
        } else if (C2Definitions.SecType.OPT.equals(inputRequest.getSecType())) {
            instrument = C2ApiEnums.Instrument.OPTION;
        } else if (C2Definitions.SecType.FUT.equals(inputRequest.getSecType())) {
            instrument = C2ApiEnums.Instrument.FUTURE;
        } else if (C2Definitions.SecType.CASH.equals(inputRequest.getSecType())) {
            instrument = C2ApiEnums.Instrument.FOREX;
        }
        c2Signal.setInstrument(instrument);
    }

    private void determineSymbol(C2Signal c2Signal, InputRequest inputRequest) {
        String symbol = inputRequest.getSymbol();
        if (C2ApiEnums.Instrument.FUTURE.equals(c2Signal.getInstrument())) {
            symbol = ConversionUtil.convertFutureSymbol(symbol);
        } else if (C2ApiEnums.Instrument.OPTION.equals(c2Signal.getInstrument())) {
            symbol = ConversionUtil.convertOptionSymbol(symbol);
        } else if (C2ApiEnums.Instrument.FOREX.equals(c2Signal.getInstrument())) {
            symbol = ConversionUtil.convertForexSymbol(symbol);
        }
        c2Signal.setSymbol(symbol);
    }

    private void determineQuant(C2Signal c2Signal, Integer position, InputRequest inputRequest) {
        Integer quant = inputRequest.getQuantity();
        Integer reversalQuant = 0;
        C2Definitions.ReversalSignalType reversalSignalType = C2Definitions.ReversalSignalType.NONE;
        if (C2ApiEnums.Instrument.FOREX.equals(c2Signal.getInstrument())) {
            quant = ConversionUtil.convertForexQuant(quant);
            quant = (quant < 1 ? 1 : quant);
        }
        int newPosition = (C2Definitions.Action.BUY.equals(inputRequest.getAction()) ? position + quant : position - quant);
        boolean isReversal = (position < 0 && newPosition > 0) || (position > 0 && newPosition < 0);
        if (isReversal) {
            quant = (position > 0 ? position : -position);
            reversalQuant = (newPosition > 0 ? newPosition : -newPosition);
            reversalSignalType = C2Definitions.ReversalSignalType.PARENT;
        }
        c2Signal.setQuant(quant);
        c2Signal.setReversalQuant(reversalQuant);
        c2Signal.setReversalSignalType(reversalSignalType);
    }

    private void determineAction(C2Signal c2Signal, Integer position, InputRequest inputRequest) {
        c2Signal.setAction(C2Definitions.Action.BUY.equals(inputRequest.getAction()) ? (position >= 0 ? C2ApiEnums.Action.BTO : C2ApiEnums.Action.BTC) : (position > 0 ? C2ApiEnums.Action.STC : C2ApiEnums.Action.STO));
    }

    private void determineDuration(C2Signal c2Signal, InputRequest inputRequest) {
        C2ApiEnums.Duration duration;
        if (C2Definitions.Tif.DAY.equals(inputRequest.getTif())) {
            duration = C2ApiEnums.Duration.DAY;
        } else if (C2Definitions.Tif.GTC.equals(inputRequest.getTif())) {
            duration = C2ApiEnums.Duration.GTC;
        } else {
            duration = C2ApiEnums.Duration.DAY;
        }
        c2Signal.setDuration(duration);
    }
}
