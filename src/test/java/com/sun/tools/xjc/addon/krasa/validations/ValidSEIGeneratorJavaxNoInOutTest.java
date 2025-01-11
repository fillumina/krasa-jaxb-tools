package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJavaxNoInOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJavaxNoInOutTest() {
        super(ValidationsAnnotation.JAVAX, null);
    }

    /**
     * Same behavior as InOut because that's the default.
     */
    @Test
    public void shouldNoInOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
