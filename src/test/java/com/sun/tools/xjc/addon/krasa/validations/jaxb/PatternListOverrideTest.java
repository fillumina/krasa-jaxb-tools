package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/** A statement selecting a property must not discard its nested patterns. */
public class PatternListOverrideTest extends AnnotationCheckerFixtureTest {

    public PatternListOverrideTest(ValidationsAnnotation library) {
        super(library, "multiplePatternsWithMultiPatternWithBase", "a",
                "MultiplePatternsWithMultiPatternWithBaseTest");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#multiplePatternsWithBase@NotNull")
                .getOptionList();
    }
}
