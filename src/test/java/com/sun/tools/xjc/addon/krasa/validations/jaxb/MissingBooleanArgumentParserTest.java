package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.FixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MissingBooleanArgumentParserTest extends FixtureTest {

    // using an existing parsed XSD
    public MissingBooleanArgumentParserTest(ValidationsAnnotation library) {
        super(library, "array", "a");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .addWithoutValue(ValidationsArgument.generateListAnnotations)
                .addWithoutValue(ValidationsArgument.generateNotNullAnnotations)
                .addWithoutValue(ValidationsArgument.verbose)
                .addWithoutValue(ValidationsArgument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
