package com.highpowerbear.hpbanalytics.c2.process;

import com.highpowerbear.hpbanalytics.c2.c2client.RequestHandler;
import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
import com.highpowerbear.hpbanalytics.c2.common.C2Util;
import com.highpowerbear.hpbanalytics.c2.entity.C2Signal;
import com.highpowerbear.hpbanalytics.c2.entity.C2System;
import com.highpowerbear.hpbanalytics.c2.entity.InputRequest;
import com.highpowerbear.hpbanalytics.c2.persistence.C2Dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 *
 * @author Robert
 */
@ApplicationScoped
public class InputProcessor {
    @Inject private C2Dao c2Dao;
    @Inject private RequestHandler requestHandler;
    @Inject private SignalCreator signalCreator;

    public void process(C2System c2System, InputRequest inputRequest) {
        C2Definitions.RequestType requestType = inputRequest.getRequestType();
        if (C2Definitions.RequestType.SUBMIT.equals(requestType)) {
            processSubmit(c2System, inputRequest);
        } else if (C2Definitions.RequestType.UPDATE.equals(requestType)) {
            processUpdate(inputRequest);
        } else if (C2Definitions.RequestType.CANCEL.equals(requestType)) {
            processCancel(inputRequest);
        }
    }

    private void processSubmit(C2System c2System, InputRequest inputRequest) {
        // delay processing input requests with ocaGroup set (target/stop bracket orders) to allow for opening order to be filled on c2 side
        if (inputRequest.getOcaGroup() != null) {
            C2Util.sleepMillis(C2Definitions.REQUESTS_WITH_OCAGROUP_DELAY);
        }
        C2Signal c2Signal = signalCreator.create(c2System, inputRequest);
        inputRequest.changeStatus(requestHandler.submit(c2Signal) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        c2Signal = c2Dao.findC2Signal(c2Signal.getId()); // refresh from DB
        if (!C2Definitions.InputStatus.ERROR.equals(inputRequest.getStatus()) && C2Definitions.ReversalSignalType.PARENT.equals(c2Signal.getReversalSignalType())) {
            C2Signal reversalSignal = signalCreator.createReversal(c2Signal);
            inputRequest.changeStatus(requestHandler.submit(reversalSignal) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        }
        c2Dao.updateInputRequest(inputRequest);
    }

    private void processCancel(InputRequest inputRequest) {
        // prevent cancelling signals with ocaGroup to keep bracket orders (target/stop) in place on c2 side, let c2 drive execution/cancellation
        if (inputRequest.getOcaGroup() != null) {
            inputRequest.changeStatus(C2Definitions.InputStatus.IGNORED);
            inputRequest.setIgnoreReason(C2Definitions.IgnoreReason.OCAGROUPSET);
            c2Dao.updateInputRequest(inputRequest);
            return;
        }
        List<C2Signal> signals = c2Dao.getC2SignalsByOriginReference(inputRequest.getOrigin(), inputRequest.getReferenceId());
        // multiple signals can be returned only in case of reversal orders, where PARENT and CHILD are referring to the same referenceId
        if (signals.isEmpty()) {
            inputRequest.changeStatus(C2Definitions.InputStatus.IGNORED);
            inputRequest.setIgnoreReason(C2Definitions.IgnoreReason.NOWORKINGSIGNAL);
            c2Dao.updateInputRequest(inputRequest);
            return;
        }
        C2Signal c2Signal = signals.get(0);
        if (signals.size() == 1) {
            inputRequest.changeStatus(requestHandler.cancel(c2Signal) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        } else {
            // in case of reversal signal, cancel only parent, child will be cancelled automatically
            C2Signal parent = (C2Definitions.ReversalSignalType.PARENT.equals(c2Signal.getReversalSignalType()) ? c2Signal : signals.get(1));
            inputRequest.changeStatus(requestHandler.cancel(parent) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        }
        c2Dao.updateInputRequest(inputRequest);
    }

    private void processUpdate(InputRequest inputRequest) {
        List<C2Signal> signals = c2Dao.getC2SignalsByOriginReference(inputRequest.getOrigin(), inputRequest.getReferenceId());
        if (signals.isEmpty()) {
            inputRequest.changeStatus(C2Definitions.InputStatus.IGNORED);
            inputRequest.setIgnoreReason(C2Definitions.IgnoreReason.NOWORKINGSIGNAL);
            c2Dao.updateInputRequest(inputRequest);
            return;
        }
        C2Signal oldSignal = signals.get(0);
        if (!C2Definitions.ReversalSignalType.NONE.equals(oldSignal.getReversalSignalType())) {
            inputRequest.changeStatus(C2Definitions.InputStatus.IGNORED);
            inputRequest.setIgnoreReason(C2Definitions.IgnoreReason.REVERSALUPDATE);
            c2Dao.updateInputRequest(inputRequest);
            return;
        }
        C2Signal updateSignal = signalCreator.createUpdated(oldSignal, inputRequest);
        inputRequest.changeStatus(requestHandler.cancelOldBecauseUpdate(oldSignal) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        if (C2Definitions.InputStatus.PROCESSED.equals(inputRequest.getStatus())) {
            inputRequest.changeStatus(requestHandler.submitNewBecauseUpdate(updateSignal) ? C2Definitions.InputStatus.PROCESSED : C2Definitions.InputStatus.ERROR);
        }
        c2Dao.updateInputRequest(inputRequest);
    }
}
