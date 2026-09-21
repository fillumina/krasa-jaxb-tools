package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * The custom message is the class and field name followed by the message key of the library this run
 * generates for.
 *
 * @author Francesco Illuminati
 */
public class ClassNameTest extends AnnotationCheckerFixtureTest {

    public ClassNameTest(ValidationsAnnotation library) {
        super(library, "notNull", "a", true, "NotNullType");
    }

    @Override
    protected void checkGeneratedAnnotations() {
        try {
            withElement("NotNullType")
                    .withField("notNullString")
                    .withAnnotation("NotNull")
                    .assertParam("message",
                            "NotNullType.notNullString {" + getAnnotationLibraryName()
                                    + ".validation.constraints.NotNull.message}");
        } catch (Throwable throwable) {
            findings.addError(throwable);
        }
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, "ClassName")
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
