package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class DefaultOptionTest extends RunXJC2MojoTestHelper {

    public DefaultOptionTest() {
        super("abase", "");
    }

    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

}