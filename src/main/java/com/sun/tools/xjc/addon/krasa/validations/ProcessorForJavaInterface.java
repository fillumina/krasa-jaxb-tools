package com.sun.tools.xjc.addon.krasa.validations;

import java.lang.annotation.Annotation;
import org.apache.cxf.tools.common.model.JavaInterface;

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
