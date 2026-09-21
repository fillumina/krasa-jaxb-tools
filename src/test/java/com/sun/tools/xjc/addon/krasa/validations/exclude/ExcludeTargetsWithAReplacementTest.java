package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A targeted statement takes a replacement as well, and the annotations it did not name stay.
 *
 * @author Francesco Illuminati
 */
public class ExcludeTargetsWithAReplacementTest extends AnnotationCheckerTestHelper {

    public ExcludeTargetsWithAReplacementTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label@NotNull=@NotNull(message = \"required\")")
                .getOptionList();
    }

}
