package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
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
