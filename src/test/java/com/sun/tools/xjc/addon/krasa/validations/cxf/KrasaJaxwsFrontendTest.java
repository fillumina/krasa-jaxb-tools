package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 * The two frontends this plugin ships, generating from the same WSDL.
 *
 * <p>
 * The {@code krasa} frontend must keep generating exactly what it generated before
 * {@code krasa-jaxws} existed; the new one adds the CXF generators to it, so the CXF stubs appear
 * next to the validated interface.
 *
 * @author Francesco Illuminati
 */
public class KrasaJaxwsFrontendTest {

    private static final String WSDL = "/generators-example.wsdl";
    private static final String INTERFACE = "com/example/ws/ExampleWS.java";
    private static final String SERVICE = "com/example/ws/ExampleWSService.java";
    private static final String FAULT = "com/example/ws/CustomException_Exception.java";

    @Test
    public void shouldKrasaFrontendGenerateTheValidatedInterfaceOnly() {
        new ValidSEIGeneratorTestHelper(ValidationsAnnotation.JAVAX, "InOut",
                WSDL, "krasa", "krasa-frontend")
                .assertGeneratedFileExists(INTERFACE)
                .assertGeneratedFileAbsent(SERVICE)
                .assertGeneratedFileAbsent(FAULT)
                .withGeneratedInterface(INTERFACE)
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("userRequest")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("arg4")
                        .withAnnotation("Valid").assertNoParameters();
    }

    @Test
    public void shouldKrasaJaxwsFrontendGenerateTheCxfStubsAndTheValidatedInterface() {
        new ValidSEIGeneratorTestHelper(ValidationsAnnotation.JAVAX, "InOut",
                WSDL, "krasa-jaxws", "krasa-jaxws-frontend")
                .assertGeneratedFileExists(SERVICE)
                .assertGeneratedFileExists(FAULT)
                .withGeneratedInterface(INTERFACE)
                .assertImport(ValidationsAnnotation.JAVAX.getValidClass())
                .withMethod("userRequest")
                    .withAnnotation("Valid").assertNoParameters().end()
                    .withParameter("arg4")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
