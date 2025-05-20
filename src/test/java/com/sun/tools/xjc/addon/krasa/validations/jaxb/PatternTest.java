package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import org.junit.Test;

public class PatternTest extends AnnotationCheckerTestHelper {

    public PatternTest() {
        super("pattern", "a", "ProcessContainer");
    }

    @Test
    public void enumerationTypeShouldNotHavePatternAnnotation() {
        withElement("ProcessContainer")
                .withField("processs")
                        .assertAnnotationNotPresent("Pattern")
                        .end()
                .withField("someList")
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("EachPattern").assertParam("regexp", "[SsOoMmEe_RrGgXxPp]").end()
                        .withAnnotation("Size").assertValue("5");

    }

}
