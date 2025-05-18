package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
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
