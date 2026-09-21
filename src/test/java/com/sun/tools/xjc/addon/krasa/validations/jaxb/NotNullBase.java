package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

public abstract class NotNullBase extends AnnotationCheckerFixtureTest {
    private Object notNullAnnotationsCustomMessage = false; // default

    public NotNullBase(ValidationsAnnotation library, Object notNullAnnotationsCustomMessage) {
        super(library, "notNull", "a", true, "NotNullType");
        this.notNullAnnotationsCustomMessage = notNullAnnotationsCustomMessage;
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, notNullAnnotationsCustomMessage)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
