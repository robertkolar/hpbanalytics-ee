package com.highpowerbear.hpbanalytics.report.process;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by robertk on 10/10/2016.
 */
@Singleton
public class ReportScheduler {

    @Inject private ExchangeRateRetriever exchangeRateRetriever;

    @Schedule(hour = "01", timezone="US/Eastern", persistent=false)
    private void retrieveExchangeRates() {
        exchangeRateRetriever.retrieve();
    }
}
