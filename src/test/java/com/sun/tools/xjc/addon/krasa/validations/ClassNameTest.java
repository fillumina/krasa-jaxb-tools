package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ClassNameTest extends AnnotationCheckerTestHelper {

    public ClassNameTest() {
        super("notNull", "a", true, "NotNullType");
    }

    @Override
    public void checkJavax() {
        withElement("NotNullType")
                .withField("notNullString")
                .withAnnotation("NotNull")
                .assertParam("message",
                        "NotNullType.notNullString {javax.validation.constraints.NotNull.message}");
    }

    @Override
    public void checkJakarta() {
        withElement("NotNullType")
                .withField("notNullString")
                .withAnnotation("NotNull")
                .assertParam("message",
                        "NotNullType.notNullString {jakarta.validation.constraints.NotNull.message}");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, "ClassName")
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
