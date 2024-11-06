package com.sun.tools.xjc.addon.krasa.validations;

import org.apache.cxf.tools.common.model.JavaInterface;

import java.lang.annotation.Annotation;

public class ProcessorForJavaInterface {
    private final Class<? extends Annotation> validClass;
    private final ProcessorForJavaMethod processorForJavaMethod;

    public ProcessorForJavaInterface(ValidationsOptions validationsOptions) {
        this.validClass = validationsOptions.getAnnotationFactory().getValidClass();
        this.processorForJavaMethod = new ProcessorForJavaMethod(validationsOptions);
    }

    public void process(JavaInterface javaInterface) {
        javaInterface.addImport(validClass.getCanonicalName());

        javaInterface.getMethods().forEach(processorForJavaMethod::process);
    }
}
