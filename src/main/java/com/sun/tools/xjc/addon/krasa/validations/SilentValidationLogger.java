package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati 
 */
public class SilentValidationLogger implements ValidationsLogger {

    public static final SilentValidationLogger INSTANCE = new SilentValidationLogger();

    private SilentValidationLogger() {}

    @Override
    public void addAnnotation(String annotationName, Map<String, String> parameterMap) {
        // do nothing
    }

    /**
     * A warning is not the chatter this logger exists to silence: it says that something asked for
     * had no effect, so it goes to the error stream.
     */
    @Override
    public void warning(String message) {
        System.err.println(PREFIX + "warning: " + message);
    }

}
