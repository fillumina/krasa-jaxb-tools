package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidTest extends AnnotationCheckerFixtureTest {

    public ValidTest(ValidationsAnnotation library) {
        super(library, "valid", "a", "ContentListType,MessageContentType,MessageType");
    }

}
