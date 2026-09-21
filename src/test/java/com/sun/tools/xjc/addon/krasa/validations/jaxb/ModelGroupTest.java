package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

/**
 * A repeating sequence becomes a field of {@code List<Object>}: a container like any other, and
 * by default it keeps its {@code @Valid}, which is what this test pins.
 *
 * @author Francesco Illuminati
 */
public class ModelGroupTest extends AnnotationCheckerTestHelper {

    public ModelGroupTest() {
        super("modelGroup", "a", "Grouped");
    }

    /** By default the model group carries @Valid, as it always has. */
    public void testTheGroupKeepsItsValidByDefault() {
        withElement("Grouped")
                .withField("itemAndNote")
                .withAnnotation("Valid");
    }
}
