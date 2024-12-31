package com.sun.tools.xjc.addon.krasa.validations;

/**
 * Validation API 2.0 supports inclusive for @DecimalMin and @DecimalMax
 *
 * @see https://github.com/krasa/krasa-jaxb-tools/issues/38
 *
 * @author Francesco Illuminati
 */
public class InvoiceTest extends AnnotationCheckerTestHelper {

    public InvoiceTest() {
        super("invoice", "a", "Invoice");
    }

    public void test() throws ClassNotFoundException {
        withElement("Invoice")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("NotNull")
                .withField("amount")
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", false)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
