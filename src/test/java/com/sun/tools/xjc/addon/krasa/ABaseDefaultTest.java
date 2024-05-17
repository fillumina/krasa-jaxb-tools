package com.sun.tools.xjc.addon.krasa;

import java.util.Arrays;
import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ABaseDefaultTest extends ABaseTest {

    public ABaseDefaultTest() {
        super(ValidationAnnotation.JAVAX);
    }

    public String getAnnotationFileName() {
        return "ABaseDefaultTest-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return Arrays.asList("-" + JaxbValidationsPlugins.PLUGIN_OPTION_NAME);
    }

}