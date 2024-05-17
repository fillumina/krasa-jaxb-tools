package com.sun.tools.xjc.addon.krasa;

public abstract class ABaseTest extends AnnotationsMojoTestHelper {

    public ABaseTest(ValidationAnnotation annotation) {
        super("abase", annotation);
    }

    @Override
    public String getNamespace() {
        return "";
    }

    @Override
    public String getFolderName() {
        return "abase";
    }

    public void testNotNullAndSizeMax() {
        element("AddressType")
                .attribute("name")
                        .annotation("Size").assertParam("max", 50).end()
                        .annotation("NotNull").assertNoValues();
    }

    public void testNotNullAndSizeMinAndMax() {
        element("AddressType")
                .attribute("countryCode")
                        .annotation("NotNull").assertNoValues()
                        .annotation("Size")
                                .assertParam("min", 2)
                                .assertParam("max", 2);
    }

    public void testValidAndSizeMinMax() {
        element("AddressType")
                .attribute("phoneNumber")
                        .annotation("Valid").assertNoValues()
                        .annotation("Size")
                                .assertParam("min", 0)
                                .assertParam("max", 3);
    }

    public void testAnnotationNotPresent() {
        element("AddressType")
                .attribute("isDefaultOneClick")
                        .assertNoAnnotationsPresent();
    }

    public void testPattern() {
        element("EmailAddressType")
                .attribute("preferredFormat")
                        .annotation("Pattern")
                                .assertParam("regexp",
                                        "(\\\\QTextOnly\\\\E)|(\\\\QHTML\\\\E)");
    }
}
