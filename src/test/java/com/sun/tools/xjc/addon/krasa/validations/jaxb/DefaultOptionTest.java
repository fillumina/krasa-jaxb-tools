package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.FixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class DefaultOptionTest extends FixtureTest {

    public DefaultOptionTest(ValidationsAnnotation library) {
        super(library, "abase", "");
    }

    @Override
    protected List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

}