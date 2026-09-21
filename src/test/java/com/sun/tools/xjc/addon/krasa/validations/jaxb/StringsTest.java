package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class StringsTest extends AnnotationCheckerFixtureTest {

    public StringsTest(ValidationsAnnotation library) {
        super(library, "strings", "a", "Strings");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("Strings")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("address")
                        .withAnnotation("Size")
                                .assertParam("min", "21")
                                .assertParam("max", "43")
                        .end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
