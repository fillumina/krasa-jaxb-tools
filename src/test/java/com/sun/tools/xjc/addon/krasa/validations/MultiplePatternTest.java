package com.sun.tools.xjc.addon.krasa.validations;

public class MultiplePatternTest extends AnnotationCheckerTestHelper {

    public MultiplePatternTest() {
        super("multiplePatterns", "a", "Multipattern");
    }

    public void test() throws ClassNotFoundException {
        withElement("Multipattern")
                .assertImportSimpleName("Pattern")
                .withField("multiplePatterns")
                    .withAnnotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

}
