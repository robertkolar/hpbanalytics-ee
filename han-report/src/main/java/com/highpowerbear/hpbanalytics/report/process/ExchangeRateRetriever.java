package com.highpowerbear.hpbanalytics.report.process;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import com.highpowerbear.hpbanalytics.report.entity.ExchangeRate;
import com.highpowerbear.hpbanalytics.report.persistence.ReportDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 10/10/2016.
 */
@ApplicationScoped
public class ExchangeRateRetriever {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @Inject private ReportDao reportDao;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public void retrieve() {
        l.info("BEGIN ExchangeRateRetriever.retrive");
        Client client = ClientBuilder.newClient();
        for (int i = 0; i < ReportDefinitions.EXCHANGE_RATE_DAYS_BACK; i++) {
            WebTarget target = client.target(ReportDefinitions.EXCHANGE_RATE_URL);
            ExchangeRate exchangeRate = new ExchangeRate();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, i - ReportDefinitions.EXCHANGE_RATE_DAYS_BACK);
            String date = df.format(cal.getTime());
            target = target.path(date);
            DateRate dateRate = retrievePair(target, "EUR", "USD");
            exchangeRate.setDate(dateRate.date);
            exchangeRate.setEurUsd(dateRate.rate);

            exchangeRate.setGbpUsd(retrievePair(target, "GBP", "USD").rate);
            exchangeRate.setAudUsd(retrievePair(target, "AUD", "USD").rate);
            exchangeRate.setNzdUsd(retrievePair(target, "NZD", "USD").rate);
            exchangeRate.setUsdChf(retrievePair(target, "USD", "CHF").rate);
            exchangeRate.setUsdJpy(retrievePair(target, "USD", "JPY").rate);
            exchangeRate.setUsdCad(retrievePair(target, "USD", "CAD").rate);
            reportDao.createOrUpdateExchangeRate(exchangeRate);
        }
        client.close();
        l.info("END ExchangeRateRetriever.retrive");
    }

    private class DateRate {
        private String date;
        private Double rate;

        public DateRate(String date, Double rate) {
            this.date = date;
            this.rate = rate;
        }
    }

    private DateRate retrievePair(WebTarget target, String base, String symbol) {
        target = target.queryParam("base", base).queryParam("symbols", symbol);
        String response = target.request().get(String.class);
        l.info(response);
        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = (JsonObject) reader.read();
        String date = jsonObject.getJsonString("date").getString();
        Double rate = jsonObject.getJsonObject("rates").getJsonNumber(symbol).doubleValue();
        reader.close();
        return new DateRate(date, rate);
    }
}
