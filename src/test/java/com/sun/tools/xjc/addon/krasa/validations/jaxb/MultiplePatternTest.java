package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class MultiplePatternTest extends AnnotationCheckerFixtureTest {

    public MultiplePatternTest(ValidationsAnnotation library) {
        super(library, "multiplePatterns", "a", "Multipattern");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("Multipattern")
                .assertImportSimpleName("Pattern")
                .withField("multiplePatterns")
                    .withAnnotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

}
