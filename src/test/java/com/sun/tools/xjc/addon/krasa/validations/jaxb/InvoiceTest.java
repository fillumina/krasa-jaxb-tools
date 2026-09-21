package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 * Validation API 2.0 supports inclusive for @DecimalMin and @DecimalMax
 *
 * @see https://github.com/krasa/krasa-jaxb-tools/issues/38
 *
 * @author Francesco Illuminati
 */
public class InvoiceTest extends AnnotationCheckerFixtureTest {

    public InvoiceTest(ValidationsAnnotation library) {
        super(library, "invoice", "a", "Invoice");
    }

    @Test
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
