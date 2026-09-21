package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A parameter that is not a string is written as the annotation declares it, so max stays a number.
 *
 * @author Francesco Illuminati
 */
public class ExcludeSetsANumericParameterTest extends AnnotationCheckerTestHelper {

    public ExcludeSetsANumericParameterTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label@Size:max = 5")
                .getOptionList();
    }

}
