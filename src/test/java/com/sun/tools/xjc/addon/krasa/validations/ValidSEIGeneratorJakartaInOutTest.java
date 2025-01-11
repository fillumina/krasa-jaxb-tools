package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJakartaInOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJakartaInOutTest() {
        super(ValidationsAnnotation.JAKARTA, "InOut");
    }

    @Test
    public void shouldInOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAKARTA.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
