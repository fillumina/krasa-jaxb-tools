package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultOptionTest extends RunXJC2MojoTestHelper {

    public DefaultOptionTest() {
        super(ValidationsAnnotation.JAVAX);
    }

    @Override
    public String getFolderName() {
        return "abase";
    }

    @Override
    public String getAnnotationFileName() {
        return "ABaseDefaultTest-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

}