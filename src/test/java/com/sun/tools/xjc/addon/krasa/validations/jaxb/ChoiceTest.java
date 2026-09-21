package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

public class ChoiceTest extends AnnotationCheckerFixtureTest {

    public ChoiceTest(ValidationsAnnotation library) {
        super(library, "choices", "a", "Choices");
    }

    @Test
    public void test() {
        withElement("Choices")
                .withField("tea")
                        .withAnnotation("XmlElement")
                                .assertParam("name", "Tea")
                        .end()
                        // a member of a <xsd:choice> cannot be @NotNull
                        .assertAnnotationNotPresent("NotNull")
                .end()
                .withField("coffee")
                        .withAnnotation("XmlElement")
                                .assertParam("name", "Coffee")
                        .end()
                        // a member of a <xsd:choice> cannot be @NotNull
                        .assertAnnotationNotPresent("NotNull")
                .end();
    }

}
