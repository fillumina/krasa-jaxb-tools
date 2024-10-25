package com.sun.tools.xjc.addon.krasa.validations;

/**
 * Test the application of numeric restrictions to a decimal type converted to String.
 * Note that java allows those annotations on Strings.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringDigitsRestrictionTest extends RunXJC2MojoTestHelper {

    public StringDigitsRestrictionTest() {
        super("stringDigitsRestriction", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("StringClassContainer")
                .withField("stringField")
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
