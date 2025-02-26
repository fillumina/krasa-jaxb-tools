package com.sun.tools.xjc.addon.krasa.validations;

import org.apache.cxf.tools.common.CommandInterfaceUtils;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.wsdlto.WSDLToJava;

/**
 * Instruct the Apache CXF to generate classes from a given WSDL
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CxfJavaGenerator extends WSDLToJava {
    private static final String INPUT_WSDL = "/hello.wsdl";

    // used to test it
    public static void main(String[] args) {
        new CxfJavaGenerator(
                // arguments for the XCF
                new String[]{
                    "-verbose",
                    "-frontend",
                    "krasa", // enable ValidSEIGenerator
                    PathUtil.getAbsolutePathOfResource(INPUT_WSDL)
                },
                // arguments for XJC
                new String[] {
                    ValidationsArgument.validationAnnotations.withValue(ValidationsAnnotation.JAVAX.name()),
                    ValidationsArgument.generateServiceValidationAnnotations.withValue("InOut")
                },
                "xcf").execute();
    }

    private final String[] xjcArgs;
    private final String outputDir;

    public CxfJavaGenerator(String[] args, String[] xjcArgs, String outputDir) {
        super(args);
        this.xjcArgs = xjcArgs;
        this.outputDir = outputDir;
    }

    public final String getOutputDir() {
        return outputDir;
    }

    protected void execute() {
        System.setProperty("org.apache.cxf.JDKBugHacks.defaultUsesCaches", "true");

        CommandInterfaceUtils.commandCommonMain();
        try {
            final ToolContext toolContext = new ToolContext();
            toolContext.put(ToolConstants.CFG_XJC_ARGS, xjcArgs);

            String targetDir = PathUtil.getAbsolutePathOfGeneratedTestSourcesDirectory() + outputDir;
            toolContext.put(ToolConstants.CFG_OUTPUTDIR, targetDir);

            run(toolContext);

        } catch (Exception ex) {
            System.err.println("WSDLToJava Error: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
