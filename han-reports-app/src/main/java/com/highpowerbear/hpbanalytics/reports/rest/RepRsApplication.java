package com.highpowerbear.hpbanalytics.reports.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rkolar
 */
@ApplicationPath("/rest")
public class RepRsApplication extends Application {
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> classes = new HashSet<>();

    public RepRsApplication(){
        classes.add(RepService.class);
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