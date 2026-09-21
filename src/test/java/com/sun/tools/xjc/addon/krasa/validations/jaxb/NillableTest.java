package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class NillableTest extends AnnotationCheckerFixtureTest {

    public NillableTest(ValidationsAnnotation library) {
        super(library, "nillable", "a", "Nillable");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("Nillable")
                .assertImportSimpleName("NotNull")
                .withField("notNullable")
                        .withAnnotation("NotNull").assertNoParameters()
                .end()
                .withField("nullable")
                        .assertNoAnnotationsPresent();
    }

}
