package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class EachTest extends AnnotationCheckerTestHelper {

    public EachTest() {
        super("each", "a", "EachType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();    }

}
