package com.sun.tools.xjc.addon.krasa.validations;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * Make it sure that the default test is executed if no other is present.
 * It checks if the testZDefault() test has been executed in RunXJC2MojoTestHelperDefaultTest.
 * <p>
 * It will <b>fail</b> if executed as a single test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunXJC2MojoTestHelperDefaultCheckTest extends TestCase {

    public void testRunXJC2MojoTestHelperDefaulTestExecuted() {
        assertTrue(RunXJC2MojoTestHelper.executedTests
                .contains("RunXJC2MojoTestHelperDefaultTest.testZDefault"));
    }
}
