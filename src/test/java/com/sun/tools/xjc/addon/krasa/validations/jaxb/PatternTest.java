package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

public class PatternTest extends AnnotationCheckerTestHelper {

    public PatternTest() {
        super("pattern", "a", "ProcessContainer");
    }

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
