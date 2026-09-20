package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A parameter after the : is set on the computed annotation, and {max} is the value it already had.
 *
 * @author Francesco Illuminati
 */
public class ExcludeSetsAMessageTest extends AnnotationCheckerTestHelper {

    public ExcludeSetsAMessageTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label@Size:message = at most {max} characters")
                .getOptionList();
    }

}
