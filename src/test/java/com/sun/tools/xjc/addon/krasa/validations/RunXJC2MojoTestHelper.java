package com.sun.tools.xjc.addon.krasa.validations;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.AssertionFailedError;
import junit.framework.TestResult;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

/**
 * Testing helper for generated classes. It uses XJC to compile the given XSD into java classes
 * (but doesn't compile them) that can be tested.
 *
 * Each test will be executed twice: once with the JAVAX validation and the other time
 * with the JAKARTA one.
 *
 * NOTE: We cannot use reflection here because RunXJC2Mojo acts on the generation phase and the
 * generated artifacts are not compiled.
 *
 * <p>
 * The fixture itself — the schema, the generated classes and the expectations — is an
 * {@link XjcFixture}: this class only carries the JUnit3 protocol that runs the two passes, and
 * hands every finding to the result instead of throwing it.
 *
 * @author Francesco Illuminati
 */
public abstract class RunXJC2MojoTestHelper extends RunXJC2Mojo {

    /** The tests this instance has run, see {@link #getExecutedTests()}. */
    private final Set<String> executedTests = new HashSet<>();

    private final XjcFixture fixture;

    public RunXJC2MojoTestHelper(String folderName, String namespace) {
        this(folderName, namespace, false);
    }

    /**
     *
     * @param folderName the folder name containing the xsd and the -annotation.txt files
     * @param namespace the namespace used in the xsd
     * @param separateAnnotation if there are different -annotation.txt files for each annotation
     *                           (javax or jakarta).
     */
    public RunXJC2MojoTestHelper(String folderName, String namespace, boolean separateAnnotation) {
        this.fixture = new XjcFixture(getClass().getSimpleName(), folderName, namespace,
                separateAnnotation);
    }

    /**
     * Override to test JAVAX annotated code generation.
     *
     * @param result the result of the running pass, see {@link #recordFailure(TestResult, Throwable)}
     */
    public void checkJavax(TestResult result) throws Exception {}

    /**
     * Override to test JAKARTA annotated code generation.
     *
     * @param result the result of the running pass, see {@link #recordFailure(TestResult, Throwable)}
     */
    public void checkJakarta(TestResult result) throws Exception {}

    /** Override to provide bindings */
    public File getBindingDirectory() {
        return null;
    }

    @Override
    public File getGeneratedDirectory() {
        return fixture.getGeneratedDirectory();
    }

    @Override
    public File getSchemaDirectory() {
        return fixture.getSchemaDirectory();
    }

    // test with all options enabled.
    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    /** Execute the current test twice: once for each backend (Javax and Jakarta). */
    @Override
    public void run(TestResult result) {
        String name = getName();
        final String simpleName = getClass().getSimpleName();
        if (!"testExecute".equals(name) &&
                !("testZDefault".equals(name) && hasOwnTests())) {
            runValidationPass(result, ValidationsAnnotation.JAKARTA);
            runValidationPass(result, ValidationsAnnotation.JAVAX);

            executedTests.add(simpleName + "." + name);
        }
    }

    /**
     * Whether the class declares test methods of its own, other than the two inherited
     * ones: testExecute, which is neutralised, and testZDefault, the default test meant
     * for classes that have none. Asking the class itself keeps the decision independent
     * of the order in which surefire runs the classes.
     */
    private boolean hasOwnTests() {
        for (Method method : getClass().getMethods()) {
            String name = method.getName();
            if (name.startsWith("test")
                    && !"testZDefault".equals(name) && !"testExecute".equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** @return the tests this instance has executed, as {@code Class.method} names. */
    Set<String> getExecutedTests() {
        return executedTests;
    }

    /**
     * Generates the classes with one annotation library, checks the produced annotations against
     * the expected file and runs the test methods on what has been generated.
     *
     * Failures are recorded on the TestResult instead of being thrown: a mismatch must not abort
     * the class, or the second generation and the test methods would silently never run and
     * surefire would see a single, class-level error. Each check is attempted and recorded on its
     * own, so one broken check cannot hide the ones that follow it.
     */
    private void runValidationPass(TestResult result, ValidationsAnnotation annotation) {
        fixture.setAnnotation(annotation);
        try {
            fixture.generate(getArgs(), getBindingDirectory());
        } catch (Throwable throwable) {
            // nothing has been generated, so the remaining checks have nothing to inspect
            recordFailure(result, throwable);
            return;
        }
        for (Throwable throwable : fixture.checkTrace()) {
            recordFailure(result, throwable);
        }
        try {
            checkGeneratedAnnotations(annotation, result);
        } catch (Throwable throwable) {
            recordFailure(result, throwable);
        }
        super.run(result);
    }

    private void checkGeneratedAnnotations(ValidationsAnnotation annotation, TestResult result)
            throws Exception {
        if (annotation == ValidationsAnnotation.JAKARTA) {
            checkJakarta(result);
        } else {
            checkJavax(result);
        }
    }

    /**
     * Records a failure where it is found, so a check that makes more than one finding reports
     * all of them instead of aborting on the first one.
     */
    protected void recordFailure(TestResult result, Throwable throwable) {
        if (throwable instanceof AssertionFailedError) {
            result.addFailure(this, (AssertionFailedError) throwable);
        } else {
            result.addError(this, throwable);
        }
    }

    /** Default test to execute if none provided. */
    public void testZDefault() {}

    /** @return comma separated values or a single one */
    public final String getNamespace() {
        return fixture.getNamespace();
    }

    /** @return the evaluated annotation. */
    public final ValidationsAnnotation getAnnotation() {
        return fixture.getAnnotation();
    }

    /**
     * Return the evaluated annotation name.
     *
     * @return either 'javax' or 'jakarta' (in lowercase).
     * @see ValidationsAnnotation
     */
    protected final String getAnnotationLibraryName() {
        return fixture.getAnnotationLibraryName();
    }

    /** Must be overwritten to avoid starting the default test. */
    public final void testExecute() throws Exception {
        // override RunXJC2Mojo own method to allow tests to be executed after mojo creation
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param elementName The name of the root element created (the java class name created by JAXB).
     * @see #getNamespace()
     */
    public ArtifactTester<XjcFixture> withElement(String elementName) {
        return fixture.withElement(elementName);
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param namespace the namespace as in <namespace:elementname param='...'/>
     * @param elementName the name of the element without the namespace part
     * @return a tester
     */
    public ArtifactTester<XjcFixture> withElement(String namespace, String elementName) {
        return fixture.withElement(namespace, elementName);
    }

    // It's a dirty little trick to change the name of the called method in the log
    @Override
    public String getName() {
        if (getAnnotation() == null) {
            // it's called by run() to get the function name to call
            return super.getName();
        }
        // it's called by descritpor after the function name has been determined
        return super.getName() + " " + getAnnotation().name();
    }
}
