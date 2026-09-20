package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Enumeration;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

/**
 * Verifies that the harness really runs, by checking its default test
 * {@link RunXJC2MojoTestHelper#testZDefault()}.
 *
 * <p>
 * <b>What the harness does.</b> The parent of the hierarchy,
 * {@code org.jvnet.jaxb2.maven2.test.RunXJC2Mojo}, is a {@code TestCase}
 * whose {@code testExecute()} generates the classes.
 * {@link RunXJC2MojoTestHelper} overrides it to an empty final method,
 * because it has to decide itself when generation happens, and its
 * {@link RunXJC2MojoTestHelper#run(TestResult)} then performs one pass per
 * annotation library - JAKARTA, then JAVAX - each pass being generation,
 * the comparison of the produced annotations with the
 * {@code -annotation.txt} reference, the flavour check and the test methods
 * of the class. A failure is recorded on the {@code TestResult} and the run
 * continues, so the failures surefire shows are the findings, not just the
 * first one.
 *
 * <p>
 * <b>Why {@code testZDefault} exists.</b> A test class that provides no test
 * methods of its own would drive nothing: the inherited
 * {@code testExecute} is neutralised and skipped by {@code run()}.
 * {@code testZDefault} is the default test that makes such a class run the
 * passes anyway - {@link RunXJC2MojoTestHelperDefaultTest} is exactly that
 * case. In a class that declares tests of its own the passes are performed by
 * them and {@code testZDefault} is skipped, so that no class generates twice.
 *
 * <p>
 * <b>What is verified here.</b> That the machinery above ran, instead of
 * generating nothing and leaving every fixture green.
 * {@link RunXJC2MojoTestHelper#getExecutedTests()} is written by {@code run()}
 * only after both passes have completed, so an entry in it is the evidence
 * that both libraries were processed; a regression that silently stopped the
 * passes would otherwise be invisible, because the annotations that the
 * fixture tests compare would simply not be produced.
 *
 * <p>
 * <b>How it is verified.</b> The harness is executed by this test instead of
 * being assumed to have been executed by another class: surefire does not
 * control the order of the classes, so a check reading state written by
 * {@link RunXJC2MojoTestHelperDefaultTest} would depend on that order.
 *
 * @author Francesco Illuminati
 */
public class RunXJC2MojoTestHelperDefaultCheckTest extends TestCase {

    private static final String HARNESS_CLASS =
            "RunXJC2MojoTestHelperDefaultTest";
    private static final String DEFAULT_TEST = HARNESS_CLASS + ".testZDefault";

    public void testRunXJC2MojoTestHelperDefaulTestExecuted() {
        RunXJC2MojoTestHelperDefaultTest harness =
                new RunXJC2MojoTestHelperDefaultTest();
        // as TestSuite would name it: run() dispatches on this name,
        // and testZDefault is the method it lets through to the passes
        harness.setName("testZDefault");

        // the result is ours, so a failure of the generation or of
        // the trace comparison is reported here instead of being lost
        TestResult result = new TestResult();
        harness.run(result);

        if (result.failureCount() + result.errorCount() > 0) {
            fail("the harness did not complete its generation and check: "
                    + firstRecorded(result));
        }

        // executedTests is written at the end of run(), after both the
        // JAKARTA pass and the JAVAX one: an entry here is the proof
        // that the two of them were performed
        assertTrue("testZDefault has not been executed by the harness",
                harness.getExecutedTests().contains(DEFAULT_TEST));

    }

    private String firstRecorded(TestResult result) {
        // the harness records an assertion failure as a failure and
        // anything else as an error, so both lists have to be consulted
        // to report what went wrong
        Enumeration<TestFailure> recorded = result.errorCount() > 0
                ? result.errors() : result.failures();
        return recorded.hasMoreElements()
                ? String.valueOf(recorded.nextElement()) : "";
    }
}
