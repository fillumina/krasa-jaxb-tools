package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJakartaNoInOutTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJakartaNoInOutTest() {
        super(ValidationsAnnotation.JAKARTA, null);
    }

    /**
     * Same behavior as InOut because that's the default.
     */
    @Test
    public void shouldNoInOutBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAKARTA.getValidClass())
                .withMethod("getWeather")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
