package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;

import java.util.List;

public class MultiplePatternsWithMultiPatternWithBaseTest extends AnnotationCheckerTestHelper {

    public MultiplePatternsWithMultiPatternWithBaseTest() {
        super("multiplePatternsWithMultiPatternWithBase", "a", "MultiPatternWithBase");
    }


    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.multiPattern, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}