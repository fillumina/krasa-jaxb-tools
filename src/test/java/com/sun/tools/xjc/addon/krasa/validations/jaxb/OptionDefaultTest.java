package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.FixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import java.util.List;
import org.junit.Test;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionDefaultTest extends FixtureTest {

    public OptionDefaultTest(ValidationsAnnotation library) {
        super(library, "options", "a");
    }

    @Override
    protected List<String> getArgs() {
        // the base would set options of its own, so this keeps the plugin at its defaults
        return ArgumentBuilder.builder().getOptionList();
    }

    @Test
    public void testEnumerationTypeShouldNotHavePatternAnnotation() {
        withElement("OptionsType")
                .withField("enumeration")
                        .assertAnnotationNotPresent("Pattern");
    }
}