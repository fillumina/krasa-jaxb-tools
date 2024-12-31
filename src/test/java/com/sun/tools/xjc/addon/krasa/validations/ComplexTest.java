package com.sun.tools.xjc.addon.krasa.validations;

public class ComplexTest extends AnnotationCheckerTestHelper {

    public ComplexTest() {
        super("abase", "", false, "AddressType");
    }

    public void testNotNullAndSizeMax() {
        withElement("AddressType")
                .withField("name")
                        .withAnnotation("Size").assertParam("max", 50).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

    public void testNotNullAndSizeMinAndMax() {
        withElement("AddressType")
                .withField("countryCode")
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("Size")
                                .assertParam("min", 2)
                                .assertParam("max", 2);
    }

    public void testValidAndSizeMinMax() {
        withElement("AddressType")
                .withField("phoneNumber")
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("Size")
                                .assertParam("min", 0)
                                .assertParam("max", 3);
    }

    public void testAnnotationNotPresent() {
        withElement("AddressType")
                .withField("isDefaultOneClick")
                        .assertNoAnnotationsPresent();
    }

    public void testPattern() {
        withElement("EmailAddressType")
                .withField("preferredFormat")
                        .withAnnotation("Pattern")
                                .assertParam("regexp",
                                        "(\\\\QTextOnly\\\\E)|(\\\\QHTML\\\\E)");
    }
}
