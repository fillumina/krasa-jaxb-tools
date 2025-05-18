package com.sun.tools.xjc.addon.krasa.validations;

import java.util.LinkedHashSet;
import java.util.Set;
import junit.framework.TestResult;

/**
 *
 * Make it sure that the default test is executed if no other are present.
 *
 * @author Francesco Illuminati
 */
public class RunXJC2MojoTestHelperTest extends RunXJC2MojoTestHelper {

    private static Set<String> executed = new LinkedHashSet<>();

    public RunXJC2MojoTestHelperTest() {
        super("array", "a");
    }


    @Override
    public void run(TestResult result) {
        executed.add(getName());

        super.run(result);
    }

}
