package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;
import java.util.List;
import org.junit.Test;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionDefaultTest extends RunXJC2MojoTestHelper {

    public OptionDefaultTest() {
        super("options", "a");
    }

    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

    @Test
    public void enumerationTypeShouldNotHavePatternAnnotation() {
        withElement("OptionsType")
                .withField("enumeration")
                        .assertAnnotationNotPresent("Pattern");
    }
}