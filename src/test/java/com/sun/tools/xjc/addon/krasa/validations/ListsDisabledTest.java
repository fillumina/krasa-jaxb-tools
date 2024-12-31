package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ListsDisabledTest extends AnnotationCheckerTestHelper {

    public ListsDisabledTest() {
        super("lists", "a", "AddressType,Container");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateListAnnotations, false)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
