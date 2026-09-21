package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

/**
 *
 * @author Francesco Illuminati
 */
public class NumericTest extends AnnotationCheckerFixtureTest {

    public NumericTest(ValidationsAnnotation library) {
        super(library, "numeric", "a", "Numeric");
    }

}
