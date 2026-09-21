package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class ComplexTest extends AnnotationCheckerFixtureTest {

    public ComplexTest(ValidationsAnnotation library) {
        super(library, "abase", "", false, "AddressType");
    }

    @Test
    public void testNotNullAndSizeMax() {
        withElement("AddressType")
                .withField("name")
                        .withAnnotation("Size").assertParam("max", 50).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

    @Test
    public void testNotNullAndSizeMinAndMax() {
        withElement("AddressType")
                .withField("countryCode")
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("Size")
                                .assertParam("min", 2)
                                .assertParam("max", 2);
    }

    @Test
    public void testValidAndSizeMinMax() {
        withElement("AddressType")
                .withField("phoneNumber")
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("Size")
                                .assertParam("min", 0)
                                .assertParam("max", 3);
    }

    @Test
    public void testAnnotationNotPresent() {
        withElement("AddressType")
                .withField("isDefaultOneClick")
                        .assertNoAnnotationsPresent();
    }

    @Test
    public void testPattern() {
        withElement("EmailAddressType")
                .withField("preferredFormat")
                        .withAnnotation("Pattern")
                                .assertParam("regexp",
                                        "(\\\\QTextOnly\\\\E)|(\\\\QHTML\\\\E)");
    }
}
