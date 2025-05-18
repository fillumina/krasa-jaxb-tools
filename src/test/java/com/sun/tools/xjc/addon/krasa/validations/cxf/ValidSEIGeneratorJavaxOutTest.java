package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
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
