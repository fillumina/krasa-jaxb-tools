package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The annotation name is a glob too, so one statement can leave out a family of them.
 *
 * @author Francesco Illuminati
 */
public class ExcludeTargetsAGlobTest extends AnnotationCheckerTestHelper {

    public ExcludeTargetsAGlobTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#amount@Decimal*")
                .getOptionList();
    }

}
