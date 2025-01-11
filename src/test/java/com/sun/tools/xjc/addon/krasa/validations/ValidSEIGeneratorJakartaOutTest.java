package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJakartaOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJakartaOutTest() {
        super(ValidationsAnnotation.JAKARTA, "Out");
    }

    @Test
    public void shouldOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAKARTA.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .assertAnnotationNotPresent("Valid");
    }
}
