package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class ArrayTest extends AnnotationCheckerFixtureTest {

    public ArrayTest(ValidationsAnnotation library) {
        super(library, "array", "a", "Array");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("Array")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("arrayOfBytes")
                        .withAnnotation("Size").assertParam("max", 18).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
