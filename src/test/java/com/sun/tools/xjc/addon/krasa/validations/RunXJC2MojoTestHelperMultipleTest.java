package com.sun.tools.xjc.addon.krasa.validations;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Make it sure all defined tests are executed on both backends (Javax and Jakarta).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunXJC2MojoTestHelperMultipleTest extends RunXJC2MojoTestHelper {

    // must be static because every test initializes a new object
    private static Set<String> executed = new LinkedHashSet<>();

    public RunXJC2MojoTestHelperMultipleTest() {
        super("array", "a");
    }


    public void testA() {
        executed.add(getName());
    }

    public void testB() {
        executed.add(getName());
    }

    // tests are executed in alphabetic order so this one should be executed last
    public void testZ() {
        assertEquals(4, executed.size());
        assertTrue(executed.contains("testA JAVAX"));
        assertTrue(executed.contains("testA JAKARTA"));
        assertTrue(executed.contains("testB JAVAX"));
        assertTrue(executed.contains("testB JAKARTA"));
    }

}
