package com.highpowerbear.hpbanalytics.iblogger.common;

import com.highpowerbear.hpbanalytics.iblogger.entity.IbAccount;
import com.highpowerbear.hpbanalytics.iblogger.entity.IbOrder;
import com.highpowerbear.hpbanalytics.iblogger.model.IbConnection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by robertk on 3/28/15.
 */
@Named
@ApplicationScoped
public class IbLoggerData {
    // ibaccount transient data (not stored in DB)
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>();
    // checking if open order still exists on IB side and declaring it UNKNOWN if it doesn't exist any more
    private Map<IbAccount, Map<IbOrder, Integer>> openOrderHeartbeatMap = new ConcurrentHashMap<>(); // ibAccount --> (ibOrder --> number of failed heartbeats left before UNKNOWN)

    // getters
    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    public Map<IbAccount, Map<IbOrder, Integer>> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }
}
