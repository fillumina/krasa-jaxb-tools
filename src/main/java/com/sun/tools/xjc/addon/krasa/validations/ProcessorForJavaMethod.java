package com.sun.tools.xjc.addon.krasa.validations;

import org.apache.cxf.tools.common.model.JAnnotation;
import org.apache.cxf.tools.common.model.JavaMethod;
import org.apache.cxf.tools.common.model.JavaParameter;

public class ProcessorForJavaMethod {

    private static final String NAME = ProcessorForJavaMethod.class.getSimpleName();
    private static final String LOG_PREFIX = NAME + ": ";
    private static final String VALID_PARAM = "VALID_PARAM";
    private static final String VALID_RETURN = "VALID_RETURN";

    private final ValidationsOptions validationsOptions;
    private final JAnnotation validAnnotationClass;

    public ProcessorForJavaMethod(ValidationsOptions validationsOptions) {
        this.validationsOptions = validationsOptions;
        this.validAnnotationClass = new JAnnotation(
                validationsOptions.getAnnotationFactory().getValidClass());
    }

    public void process(JavaMethod javaMethod) {
        if (validationsOptions.isValidOut()) {
            log("adding annotation to " + javaMethod.getSignature());
            javaMethod.addAnnotation(VALID_RETURN, validAnnotationClass);
        }

        javaMethod.getParameters().forEach(this::process);
    }

    private void process(JavaParameter javaParameter) {
        if (validationsOptions.isValidIn() && (javaParameter.isIN() || javaParameter.isINOUT())) {
            log("adding in " + javaParameter.getName());
            javaParameter.addAnnotation(VALID_PARAM, validAnnotationClass);
        }
        if (validationsOptions.isValidOut() && (javaParameter.isOUT() || javaParameter.isINOUT())) {
            log("adding out " + javaParameter.getName());
            javaParameter.addAnnotation(VALID_RETURN, validAnnotationClass);
        }
    }

    void log(String message) {
        if (validationsOptions.isVerbose()) {
            System.out.println(LOG_PREFIX + message);
        }
    }
}
