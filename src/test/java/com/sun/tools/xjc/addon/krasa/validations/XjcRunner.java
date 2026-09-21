package com.sun.tools.xjc.addon.krasa.validations;

import java.io.File;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

/**
 * One generation run of the XJC plugin, driven by composition instead of inheritance.
 *
 * <p>
 * The parent {@code RunXJC2Mojo} is a {@code TestCase}, and generation happens through its
 * {@code testExecute()}, which is {@code initMojo().execute()}. A test class that extends it can only
 * be driven through the JUnit lifecycle, which a plain test does not have; this class takes the same
 * settings and calls the same two methods, so a run is available to either kind of test.
 *
 * @author Francesco Illuminati
 */
class XjcRunner {

    private final RunXJC2Mojo runner;

    /**
     * @param schemaDirectory the directory holding the schema
     * @param generatedDirectory where the generated classes are written
     * @param args the plugin's command line arguments
     * @param bindingDirectory the bindings, or {@code null} when the fixture needs none
     */
    XjcRunner(File schemaDirectory, File generatedDirectory, List<String> args, File bindingDirectory) {
        this.runner = new RunXJC2Mojo() {

            @Override
            public File getSchemaDirectory() {
                return schemaDirectory;
            }

            @Override
            protected File getGeneratedDirectory() {
                return generatedDirectory;
            }

            @Override
            public List<String> getArgs() {
                return args;
            }

            /**
             * The parameter is raw because the inherited method declares it raw: a wildcard
             * parameter has the same erasure without overriding it.
             */
            @Override
            @SuppressWarnings("rawtypes")
            protected void configureMojo(AbstractXJC2Mojo mojo) {
                super.configureMojo(mojo);
                mojo.setProject(new MavenProject());
                mojo.setForceRegenerate(true);
                mojo.setExtension(true);
                if (bindingDirectory != null) {
                    mojo.setBindingDirectory(bindingDirectory);
                }
            }
        };
    }

    /** Generates the classes, the way the parent's test entry point does. */
    void run() throws Exception {
        runner.initMojo().execute();
    }
}
