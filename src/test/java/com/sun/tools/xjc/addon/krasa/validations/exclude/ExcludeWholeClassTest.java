package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * Without a # the statement covers the whole class, named as the generated class is named.
 *
 * @author Francesco Illuminati
 */
public class ExcludeWholeClassTest extends AnnotationCheckerFixtureTest {

    public ExcludeWholeClassTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=a.ChildType")
                .getOptionList();
    }

}
