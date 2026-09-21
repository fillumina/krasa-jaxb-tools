package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

public class PatternWithBaseTest extends AnnotationCheckerFixtureTest {

    public PatternWithBaseTest(ValidationsAnnotation library) {
        super(library, "patternWithBase", "a", "PatternWithBase");
    }

}