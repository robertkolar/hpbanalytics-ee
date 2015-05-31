package com.highpowerbear.hpbanalytics.c2;

import com.highpowerbear.hpbanalytics.c2.common.C2Definitions;
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
public class C2Lifecycle {
    private static final Logger l = Logger.getLogger(C2Definitions.LOGGER);

    @PostConstruct
    public void startup() {
        l.info("BEGIN C2Lifecycle.startup");
        l.info("END C2Lifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN C2Lifecycle.shutdown");
        l.info("END C2Lifecycle.shutdown");
    }
}
