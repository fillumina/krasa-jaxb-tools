package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import org.junit.Test;

import java.util.List;

public class MultiplePatternWithMultiPatternTest extends AnnotationCheckerFixtureTest {

    public MultiplePatternWithMultiPatternTest(ValidationsAnnotation library) {
        super(library, "multiplePatternWithMultiPattern", "a", "Multipattern");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("Multipattern")
                .assertImportSimpleName("Pattern")
                .withField("multiplePatterns")
                    .withAnnotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.multiPattern, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
