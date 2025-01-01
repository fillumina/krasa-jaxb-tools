package com.sun.tools.xjc.addon.krasa.validations;

import org.apache.cxf.tools.common.model.JavaModel;

public class ProcessorForJavaModel {
    private final ProcessorForJavaInterface processorForJavaInterface;

    public ProcessorForJavaModel(ValidationsOptions validationsOptions) {
        this.processorForJavaInterface = new ProcessorForJavaInterface(validationsOptions);
    }

    public void process(JavaModel javaModel) {
        javaModel
                .getInterfaces()
                .values()
                .forEach(processorForJavaInterface::process);
    }
}
