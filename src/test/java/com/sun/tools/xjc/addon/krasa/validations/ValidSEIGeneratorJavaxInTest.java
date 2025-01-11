package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJavaxInTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJavaxInTest() {
        super(ValidationsAnnotation.JAVAX, "In");
    }

    @Test
    public void shouldInBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("getWeather")
                    .assertAnnotationNotPresent("Valid").end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
