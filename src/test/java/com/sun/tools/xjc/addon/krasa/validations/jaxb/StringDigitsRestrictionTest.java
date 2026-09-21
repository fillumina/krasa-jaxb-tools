package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.FixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 * Test the application of numeric restrictions to a decimal type converted to String.
 * Note that java allows those annotations on Strings.
 *
 * @author Francesco Illuminati
 */
public class StringDigitsRestrictionTest extends FixtureTest {

    public StringDigitsRestrictionTest(ValidationsAnnotation library) {
        super(library, "stringDigitsRestriction", "a");
    }

    @Test
    public void test() {
        withElement("StringClassContainer")
                .withField("stringField")
                        .assertType("String")
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", "0")
                                .assertParam("inclusive", "true")
                                .end()
                        .withAnnotation("DecimalMax")
                                .assertParam("value", "99.9")
                                .assertParam("inclusive", "false")
                                .end()
                        .withAnnotation("Digits")
                                .assertParam("integer", "4")
                                .assertParam("fraction", "1")
                                .end();

    }
}
