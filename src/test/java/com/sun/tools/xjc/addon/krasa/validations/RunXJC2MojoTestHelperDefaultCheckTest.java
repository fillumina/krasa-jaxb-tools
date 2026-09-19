package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Enumeration;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import static junit.framework.TestCase.assertTrue;

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
 * case. The name is not cosmetic: the harness assumes that the methods of a
 * class are executed in alphabetical order, and it skips
 * {@code testZDefault} in a class that already ran, to avoid a third,
 * redundant generation.
 *
 * <p>
 * <b>What is verified here.</b> That the machinery above ran, instead of
 * generating nothing and leaving every fixture green.
 * {@link RunXJC2MojoTestHelper#executedTests} is written by {@code run()}
 * only after both passes have completed, so an entry in it is the evidence
 * that both libraries were processed; a regression that silently stopped the
 * passes would otherwise be invisible, because the annotations that the
 * fixture tests compare would simply not be produced.
 *
 * <p>
 * <b>How it is verified.</b> The harness class is executed by this test
 * rather than assumed to have been executed by another class before:
 * surefire does not control the order of the classes, so reading state
 * written by {@link RunXJC2MojoTestHelperDefaultTest} made the outcome depend
 * on that order - it passed in the default and in the reverse alphabetical
 * order, and failed under {@code -Dsurefire.runOrder=alphabetical}, where
 * this class runs first and finds the state empty.
 *
 * <p>
 * Note that the two static sets are what P3.2 is about: if they are ever
 * replaced by instance state, this check has to assert on the instance it ran
 * instead.
 *
 * @author Francesco Illuminati
 */
public class RunXJC2MojoTestHelperDefaultCheckTest extends TestCase {

    private static final String HARNESS_CLASS =
            "RunXJC2MojoTestHelperDefaultTest";
    private static final String DEFAULT_TEST = HARNESS_CLASS + ".testZDefault";

    public void testRunXJC2MojoTestHelperDefaulTestExecuted() {
        // run() skips testZDefault of a class that is already in
        // executions, so the record of a previous run must go first:
        // otherwise the assertions below would be satisfied by a
        // record written elsewhere, and would prove something only
        // when this check happened to run first.
        forgetHarnessRun();

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
                RunXJC2MojoTestHelper.executedTests.contains(DEFAULT_TEST));

        // leave the bookkeeping as it was found: the entry added above
        // would make the harness skip testZDefault when surefire runs
        // the class for real, which changes the number of executed
        // tests depending on the order (159, or 157 when this runs first)
        forgetHarnessRun();
    }

    private void forgetHarnessRun() {
        RunXJC2MojoTestHelper.executions.remove(HARNESS_CLASS);
        RunXJC2MojoTestHelper.executedTests.remove(DEFAULT_TEST);
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
