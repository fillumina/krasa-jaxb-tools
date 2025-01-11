package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class EmptyBooleanArgumentParserTest extends RunXJC2MojoTestHelper {

    // using an existing parsed XSD
    public EmptyBooleanArgumentParserTest() {
        super("array", "a");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateListAnnotations, "")
                .add(ValidationsArgument.generateNotNullAnnotations, "")
                .add(ValidationsArgument.verbose, "")
                .add(ValidationsArgument.generateServiceValidationAnnotations, "")
                .getOptionList();
    }


}
