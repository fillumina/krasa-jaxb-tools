package com.sun.tools.xjc.addon.krasa.validations;

public class ChoiceTest extends AnnotationCheckerTestHelper {

    public ChoiceTest() {
        super("choices", "a", "Choices");
    }

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
