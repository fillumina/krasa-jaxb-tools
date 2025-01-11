package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionGenerateStringListAnnotationTest extends AnnotationCheckerTestHelper {

    public OptionGenerateStringListAnnotationTest() {
        super("options", "a", "OptionsType");
    }

    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, false)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, null)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}