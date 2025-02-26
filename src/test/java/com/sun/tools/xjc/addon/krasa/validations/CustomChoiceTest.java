package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 * @author Niklas Neesen <niklas.neesen@enghouse.com>
 * @since 2025.02.26
 */
public class CustomChoiceTest extends CustomChoiceSEIGeneratorTestHelper {

    public CustomChoiceTest() {
        super(ValidationsAnnotation.JAKARTA, null);
    }

    @Test
    public void name() {
        withGeneratedInterface("com/example/customchoiceweather/LocationType.java")
                .assertImport(ValidationsAnnotation.JAKARTA.getValidClass())
                .withField("cityOrState")
                .withAnnotation("Valid").assertNoParameters().end();
    }
}
