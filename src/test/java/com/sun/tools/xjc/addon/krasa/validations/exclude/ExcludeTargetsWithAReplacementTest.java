package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A targeted statement takes a replacement as well, and the annotations it did not name stay.
 *
 * @author Francesco Illuminati
 */
public class ExcludeTargetsWithAReplacementTest extends AnnotationCheckerFixtureTest {

    public ExcludeTargetsWithAReplacementTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#label@NotNull=@NotNull(message = \"required\")")
                .getOptionList();
    }

}
