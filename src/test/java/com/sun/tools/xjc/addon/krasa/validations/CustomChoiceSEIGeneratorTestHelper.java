package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;

/**
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CustomChoiceSEIGeneratorTestHelper extends CxfJavaGeneratorTestHelper {
    private static final String INPUT_WSDL = "/customChoice/customChoice.wsdl";
    private static final String CLASS_FILE = "com/example/customChoiceWeather/WeatherServicePortType.java";

    public CustomChoiceSEIGeneratorTestHelper(ValidationsAnnotation validationsAnnotation, String inout) {
        super(new String[]{
                "-b",
                PathUtil.getAbsolutePathOfResource("/customChoice/jaxb-binding.xml"),
                "-verbose",
                "-frontend",
                "krasa",
                PathUtil.getAbsolutePathOfResource(INPUT_WSDL)
        }, generateXjcArgs(validationsAnnotation, inout), "xcf");

    }

    private static String[] generateXjcArgs(ValidationsAnnotation validationsAnnotation, String inout) {
        if (inout != null) {
            return new String[]{
                    JaxbValidationsPlugin.PLUGIN_OPTION_NAME,
                    ValidationsArgument.validationAnnotations.withValue(validationsAnnotation.name()),
                    ValidationsArgument.generateServiceValidationAnnotations.withValue(inout)
            };
        } else {
            return new String[]{
                    JaxbValidationsPlugin.PLUGIN_OPTION_NAME,
                    ValidationsArgument.generateNotNullAnnotations.withValue("true"),
                    ValidationsArgument.validationAnnotations.withValue(validationsAnnotation.name()),
            };
        }
    }

    public ArtifactTester<CxfJavaGeneratorTestHelper> withGeneratedInterface() {
        return withGeneratedInterface(CLASS_FILE);
    }
}
