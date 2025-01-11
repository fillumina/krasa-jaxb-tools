package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJavaxOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJavaxOutTest() {
        super(ValidationsAnnotation.JAVAX, "Out");
    }

    @Test
    public void shouldOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .assertAnnotationNotPresent("Valid");
    }
}
