package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJavaxInOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJavaxInOutTest() {
        super(ValidationsAnnotation.JAVAX, "InOut");
    }

    @Test
    public void shouldInOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
