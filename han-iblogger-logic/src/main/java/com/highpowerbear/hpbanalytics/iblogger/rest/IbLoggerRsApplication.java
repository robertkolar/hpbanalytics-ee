package com.highpowerbear.hpbanalytics.iblogger.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertk on 3/28/15.
 */
@ApplicationPath("/rest")
public class IbLoggerRsApplication extends Application {
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> classes = new HashSet<>();

    public IbLoggerRsApplication() {
        classes.add(IbLoggerService.class);
        // singletons.add(new RestService());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
