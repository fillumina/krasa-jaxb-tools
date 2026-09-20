package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A replacement is written in place of the computed annotations, and {max} is the value the plugin was about to write.
 *
 * @author Francesco Illuminati
 */
public class ExcludeRewritesSameAnnotationTest extends AnnotationCheckerTestHelper {

    public ExcludeRewritesSameAnnotationTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label=@Size(max = {max})")
                .getOptionList();
    }

}
