package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The annotation after the @ is the only thing left out, so the rest of the computed annotations stay.
 *
 * @author Francesco Illuminati
 */
public class ExcludeTargetsOneAnnotationTest extends AnnotationCheckerTestHelper {

    public ExcludeTargetsOneAnnotationTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label@NotNull")
                .getOptionList();
    }

}
