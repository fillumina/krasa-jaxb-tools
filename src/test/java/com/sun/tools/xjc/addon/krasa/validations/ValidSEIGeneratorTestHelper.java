package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidSEIGeneratorTestHelper extends CxfJavaGeneratorTestHelper {
    private static final String INPUT_WSDL = "/hello.wsdl";
    private static final String CLASS_FILE = "com/example/weather/WeatherServicePortType.java";

    public ValidSEIGeneratorTestHelper(ValidationsAnnotation validationsAnnotation, String inout) {
        this(validationsAnnotation, inout, INPUT_WSDL, "krasa", "xcf");
    }

    /**
     * Generates from another WSDL, with another frontend.
     *
     * @param wsdlResource the WSDL to generate from, as a classpath resource
     * @param frontend the CXF frontend to run, as named in {@code META-INF/tools-plugin.xml}
     * @param outputDir the directory to write to, under the generated test sources
     */
    public ValidSEIGeneratorTestHelper(ValidationsAnnotation validationsAnnotation, String inout,
            String wsdlResource, String frontend, String outputDir) {
        super(new String[]{
                    "-verbose",
                    "-frontend",
                    frontend,
                    PathUtil.getAbsolutePathOfResource(wsdlResource)
        }, generateXjcArgs(validationsAnnotation, inout), outputDir);
    }

    private static String[] generateXjcArgs(ValidationsAnnotation validationsAnnotation, String inout) {
        if (inout != null) {
            return new String[] {
                    JaxbValidationsPlugin.PLUGIN_OPTION_NAME,
                    ValidationsArgument.validationAnnotations.withValue(validationsAnnotation.name()),
                    ValidationsArgument.generateServiceValidationAnnotations.withValue(inout)
                };
        } else {
            return new String[] {
                    JaxbValidationsPlugin.PLUGIN_OPTION_NAME,
                    ValidationsArgument.validationAnnotations.withValue(validationsAnnotation.name()),
                };
        }
    }

    public ArtifactTester<CxfJavaGeneratorTestHelper> withGeneratedInterface() {
        return withGeneratedInterface(CLASS_FILE);
    }
}
