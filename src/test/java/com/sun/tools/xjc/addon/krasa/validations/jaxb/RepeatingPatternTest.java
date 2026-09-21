package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

public class RepeatingPatternTest extends AnnotationCheckerFixtureTest {

    public RepeatingPatternTest(ValidationsAnnotation library) {
        super(library, "repeatingPatterns", "a", "RepeatingPatternListType");
    }

}
