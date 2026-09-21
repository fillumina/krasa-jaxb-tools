package com.sun.tools.xjc.addon.krasa.validations;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Base class for a fixture test run by JUnit4: one schema, generated once per annotation library,
 * with the library as the test parameter so that every test method runs twice, once per library.
 *
 * <p>
 * Before each test the classes are generated and compared with the expected annotations. Every
 * finding goes to an {@link ErrorCollector} rather than aborting the run, so a mismatch does not
 * hide the findings after it and the second library is still generated, which is what the JUnit3
 * harness does with its TestResult.
 *
 * <p>
 * The inherited {@link #generatedAnnotationsAreAsExpected()} is the test a fixture gets by default,
 * the way {@code testZDefault} is for the JUnit3 harness: a class with no test method of its own
 * needs nothing but its constructor and its arguments.
 *
 * @author Francesco Illuminati
 */
@RunWith(Parameterized.class)
public abstract class FixtureTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> libraries() {
        return Arrays.asList(new Object[][] {
            { ValidationsAnnotation.JAKARTA },
            { ValidationsAnnotation.JAVAX } });
    }

    /** Holds every finding, so that one mismatch does not hide the ones after it. */
    @Rule
    public final ErrorCollector findings = new ErrorCollector();

    private final XjcFixture fixture;

    protected FixtureTest(ValidationsAnnotation library, String folderName, String namespace) {
        this(library, folderName, namespace, false);
    }

    /**
     * @param library the annotation library this run generates for
     * @param folderName the folder name containing the xsd and the -annotation.txt files
     * @param namespace the namespace used in the xsd
     * @param separateAnnotation if there are different -annotation.txt files for each annotation
     *                           (javax or jakarta).
     */
    protected FixtureTest(ValidationsAnnotation library, String folderName, String namespace,
            boolean separateAnnotation) {
        this.fixture = new XjcFixture(getClass().getSimpleName(), folderName, namespace,
                separateAnnotation);
        fixture.setAnnotation(library);
    }

    @Before
    public void generateTheClassesAndCheckTheExpectations() {
        try {
            fixture.generate(getArgs(), getBindingDirectory());
        } catch (Throwable throwable) {
            // nothing has been generated, so the remaining checks have nothing to inspect
            findings.addError(throwable);
            return;
        }
        for (Throwable throwable : fixture.checkTrace()) {
            findings.addError(throwable);
        }
        checkGeneratedAnnotations();
    }

    /**
     * The test every fixture gets: what it asserts has already been checked before it, so a failure
     * here is a finding of {@link #generateTheClassesAndCheckTheExpectations()}.
     */
    @Test
    public void generatedAnnotationsAreAsExpected() {
        // the generated classes have been compared with the expected annotations before the test
    }

    /** Override to check more about what has been generated, reporting through {@link #findings}. */
    protected void checkGeneratedAnnotations() {}

    // test with all options enabled.
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    /** Override to provide bindings */
    protected File getBindingDirectory() {
        return null;
    }

    protected ValidationsAnnotation getAnnotation() {
        return fixture.getAnnotation();
    }

    /** @return comma separated values or a single one */
    protected String getNamespace() {
        return fixture.getNamespace();
    }

    /**
     * @return either 'javax' or 'jakarta' (in lowercase).
     * @see ValidationsAnnotation
     */
    protected String getAnnotationLibraryName() {
        return fixture.getAnnotationLibraryName();
    }

    protected File getGeneratedDirectory() {
        return fixture.getGeneratedDirectory();
    }

    protected File getSchemaDirectory() {
        return fixture.getSchemaDirectory();
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param elementName The name of the root element created (the java class name created by JAXB).
     * @see #getNamespace()
     */
    protected ArtifactTester<XjcFixture> withElement(String elementName) {
        return fixture.withElement(elementName);
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param namespace the namespace as in <namespace:elementname param='...'/>
     * @param elementName the name of the element without the namespace part
     * @return a tester
     */
    protected ArtifactTester<XjcFixture> withElement(String namespace, String elementName) {
        return fixture.withElement(namespace, elementName);
    }
}
