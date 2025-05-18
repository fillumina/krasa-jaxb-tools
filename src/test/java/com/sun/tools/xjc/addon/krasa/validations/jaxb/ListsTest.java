package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

/**
 *
 * @see https://github.com/jirutka/validator-collection
 * @author Francesco Illuminati
 */
public class ListsTest extends AnnotationCheckerTestHelper {

    public ListsTest() {
        super("lists", "a", "AddressType,Container");
    }

    public void testContainer() throws ClassNotFoundException {
        withElement("Container")
                .assertImportSimpleName("Valid")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("listOfNotNullString")
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("Size")
                            .assertParam("min", 0)
                            .assertParam("max", 5).end()
                        .withAnnotation("EachSize")
                            .assertParam("min", 1)
                            .assertParam("max", 500).end()
                        .end()
                .withField("listOfAddress")
                        .withAnnotation("Size")
                            .assertParam("min", 3)
                            .assertParam("max", 7).end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("Valid").assertNoParameters()
                        .end()
                .withField("listOfPercentage")
                        .withAnnotation("Valid").assertNoParameters()
                        .withAnnotation("Size")
                            .assertParam("min", 2)
                            .assertParam("max", 4).end()
                        .withAnnotation("EachDigits")
                            .assertParam("integer", 3)
                            .assertParam("fraction", 2).end()
                        .withAnnotation("EachDecimalMin")
                            .assertParam("value", "0.00")
                            .assertParam("inclusive", false).end()
                        .withAnnotation("EachDecimalMax")
                            .assertParam("value", "100.00")
                            .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

    public void testAddressType() {
        withElement("AddressType")
                .withField("name")
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()
                .withField("formalTitle")
                        .withAnnotation("Size")
                            .assertParam("max", 10);
    }
}
