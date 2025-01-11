package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;

/**
 *
 * Make it sure that the default test is executed if no other is present.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunXJC2MojoTestHelperDefaultTest extends RunXJC2MojoTestHelper {

    public RunXJC2MojoTestHelperDefaultTest() {
        super("array", "a");
    }

}
