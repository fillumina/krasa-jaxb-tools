package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The names are globs over the generated name, so one statement can cover a whole class.
 *
 * @author Francesco Illuminati
 */
public class ExcludeGlobTest extends AnnotationCheckerFixtureTest {

    public ExcludeGlobTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*RootType#*")
                .getOptionList();
    }

}
