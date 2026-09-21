package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;

import java.util.List;

public class MultiplePatternsWithMultiPatternWithBaseTest extends AnnotationCheckerFixtureTest {

    public MultiplePatternsWithMultiPatternWithBaseTest(ValidationsAnnotation library) {
        super(library, "multiplePatternsWithMultiPatternWithBase", "a", "MultiplePatternsWithMultiPatternWithBaseTest");
    }


    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.multiPattern, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}