package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

/**
 *
 * @author Francesco Illuminati
 */
public class NumericComplexTypeTest extends AnnotationCheckerFixtureTest {

    public NumericComplexTypeTest(ValidationsAnnotation library) {
        super(library, "numericComplexType", "a", "ClassWithValidation");
    }

}
