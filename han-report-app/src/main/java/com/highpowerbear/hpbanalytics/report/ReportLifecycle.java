package com.highpowerbear.hpbanalytics.report;

import com.highpowerbear.hpbanalytics.report.common.ReportDefinitions;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

/**
 * Created by robertk on 5/31/15.
 */
@Singleton
@Startup
public class ReportLifecycle {
    private static final Logger l = Logger.getLogger(ReportDefinitions.LOGGER);

    @PostConstruct
    public void startup() {
        l.info("BEGIN RepLifecycle.startup");
        l.info("END RepLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN RepLifecycle.shutdown");
        l.info("END RepLifecycle.shutdown");
    }
}
