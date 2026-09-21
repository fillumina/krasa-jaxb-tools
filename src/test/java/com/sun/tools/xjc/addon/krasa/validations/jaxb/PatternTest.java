package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class PatternTest extends AnnotationCheckerFixtureTest {

    public PatternTest(ValidationsAnnotation library) {
        super(library, "pattern", "a", "ProcessContainer");
    }

    @Test
    public void testEnumerationTypeShouldNotHavePatternAnnotation() {
        withElement("ProcessContainer")
                .withField("process")
                        .assertAnnotationNotPresent("Pattern")
                        .end()
                .withField("someList")
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("EachPattern")
                                .assertParam("regexp", "[SsOoMmEe_RrGgXxPp]+").end()
                        .withAnnotation("Size").assertParam("max", 5);

    }

}
