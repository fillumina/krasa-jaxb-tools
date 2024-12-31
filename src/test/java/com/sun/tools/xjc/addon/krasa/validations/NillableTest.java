package com.sun.tools.xjc.addon.krasa.validations;

public class NillableTest extends AnnotationCheckerTestHelper {

    public NillableTest() {
        super("nillable", "a", "Nillable");
    }

    public void test() throws ClassNotFoundException {
        withElement("Nillable")
                .assertImportSimpleName("NotNull")
                .withField("notNullable")
                        .withAnnotation("NotNull").assertNoParameters()
                .end()
                .withField("nullable")
                        .assertNoAnnotationsPresent();
    }

}
