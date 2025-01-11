package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

public class NotNullBase extends AnnotationCheckerTestHelper {
    private Object notNullAnnotationsCustomMessage = false; // default

    public NotNullBase(Object notNullAnnotationsCustomMessage) {
        super("notNull", "a", true, "NotNullType");
        this.notNullAnnotationsCustomMessage = notNullAnnotationsCustomMessage;
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, notNullAnnotationsCustomMessage)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
