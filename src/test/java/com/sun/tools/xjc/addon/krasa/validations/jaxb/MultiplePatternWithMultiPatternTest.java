package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;

import java.util.List;

public class MultiplePatternWithMultiPatternTest extends AnnotationCheckerTestHelper {

    public MultiplePatternWithMultiPatternTest() {
        super("multiplePatternWithMultiPattern", "a", "Multipattern");
    }

    public void test() throws ClassNotFoundException {
        withElement("Multipattern")
                .assertImportSimpleName("Pattern")
                .withField("multiplePatterns")
                    .withAnnotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.multiPattern, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
