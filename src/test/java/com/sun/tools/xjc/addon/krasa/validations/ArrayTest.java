package com.sun.tools.xjc.addon.krasa.validations;

public class ArrayTest extends AnnotationCheckerTestHelper {

    public ArrayTest() {
        super("array", "a", "Array");
    }

    public void test() throws ClassNotFoundException {
        withElement("Array")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("arrayOfBytes")
                        .withAnnotation("Size").assertParam("max", 18).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
